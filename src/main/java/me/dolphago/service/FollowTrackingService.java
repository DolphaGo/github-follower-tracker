package me.dolphago.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.BaseEntity;
import me.dolphago.domain.History;
import me.dolphago.domain.HistoryRepository;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.Follower;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.domain.Following;
import me.dolphago.domain.Relation;
import me.dolphago.dto.FeignResponseDto;
import me.dolphago.dto.MemberDto;
import me.dolphago.dto.ResponseDto;
import me.dolphago.feign.GithubFeignClient;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class FollowTrackingService {
    private final GithubFeignClient client;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;
    private final HistoryRepository historyRepository;

    public List<Follower> getAllFollowers() {
        return followerRepository.findAll();
    }

    public List<Following> getAllFollowings() {
        return followingRepository.findAll();
    }

    @Transactional
    public void update(List<History> historyList) {
        // 새로운 팔로워 저장
        followerRepository.saveAll(getFollowerOf(historyList, Relation.NEW_FOLLOWER));

        // 새로운 언팔로워 삭제
        followerRepository.deleteAll(getFollowerOf(historyList, Relation.NEW_UNFOLLOWER));

        // 새로운 팔로잉 저장
        followingRepository.saveAll(getFollowingOf(historyList, Relation.NEW_FOLLOWING));

        // 새로운 언팔로잉 삭제
        followingRepository.deleteAll(getFollowingOf(historyList, Relation.NEW_UNFOLLOWING));

        // 히스토리 저장
        historyRepository.saveAll(historyList);
    }

    private List<Follower> getFollowerOf(final List<History> historyList, final Relation followerRelation) {
        return historyList.stream()
                          .filter(history -> history.getRelation() == followerRelation)
                          .map(history -> {
                                    switch (followerRelation) {
                                        case NEW_FOLLOWER:
                                            return new Follower(history.getGithubLogin(), history.getUrl());
                                        case NEW_UNFOLLOWER:
                                            return followerRepository.findByGithubLogin(history.getGithubLogin());
                                        default:
                                            return null;
                                    }
                                })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toList());
    }

    private List<Following> getFollowingOf(final List<History> historyList, final Relation followingRelation) {
        return historyList.stream()
                          .filter(history -> history.getRelation() == followingRelation)
                          .map(history -> {
                                    switch (followingRelation) {
                                        case NEW_FOLLOWING:
                                            return new Following(history.getGithubLogin(), history.getUrl());
                                        case NEW_UNFOLLOWING:
                                            return followingRepository.findByGithubLogin(history.getGithubLogin());
                                        default:
                                            return null;
                                    }
                                })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toList());
    }

    public List<Follower> getAllFollowers(String handle) {
        log.info("[Feign] github api로부터 {}의 follower 정보를 가져옵니다.", handle);
        List<Follower> followers = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) {break;}
            List<Follower> collect = body.stream()
                                         .map(dto -> new Follower(dto.getLogin(), dto.getHtml_url()))
                                         .collect(Collectors.toList());
            followers.addAll(collect);
        }
        return followers;
    }

    public List<Following> getAllFollowings(String handle) {
        log.info("[Feign] github api로부터 {}의 following 정보를 가져옵니다.", handle);
        List<Following> followings = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) {break;}
            List<Following> collect = body.stream()
                                          .map(dto -> new Following(dto.getLogin(), dto.getHtml_url()))
                                          .collect(Collectors.toList());
            followings.addAll(collect);
        }
        return followings;
    }

    public ResponseDto checkFollow(String handle) {
        ResponseDto responseDto = ResponseDto.create();

        Map<String, Follower> followerMap = getAllFollowers(handle).stream()
                                                                   .collect(Collectors.toMap(BaseEntity::getGithubLogin, Function.identity()));

        Map<String, Following> followingMap = getAllFollowings(handle).stream()
                                                                      .collect(Collectors.toMap(BaseEntity::getGithubLogin, Function.identity()));

        Map<String, BaseEntity> eachNeighborMap = new HashMap<>();

        responseDto.getNeighbors().set(
                followerMap.entrySet()
                           .stream()
                           .filter(e -> followingMap.containsKey(e.getKey()))
                           .map(e -> {
                               eachNeighborMap.put(e.getKey(), e.getValue());
                               return e.getValue();
                           }).map(f -> new MemberDto(f.getGithubLogin(), f.getUrl()))
                           .collect(Collectors.toList()));

        responseDto.getOnlyFollowers().set(followerMap.entrySet()
                                                      .stream()
                                                      .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                      .map(e -> e.getValue())
                                                      .map(followers -> MemberDto.from(followers.getGithubLogin(), followers.getUrl()))
                                                      .collect(Collectors.toList()));

        responseDto.getOnlyFollowings().set(followingMap.entrySet()
                                                        .stream()
                                                        .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                        .map(e -> e.getValue())
                                                        .map(followers -> MemberDto.from(followers.getGithubLogin(), followers.getUrl()))
                                                        .collect(Collectors.toList()));

        return responseDto;
    }

}
