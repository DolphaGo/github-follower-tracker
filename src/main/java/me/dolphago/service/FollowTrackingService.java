package me.dolphago.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.BaseEntity;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.Followers;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.domain.Followings;
import me.dolphago.domain.Neighbor;
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

    public List<Followers> getFollowersFromClient() {
        return followerRepository.findAll();
    }

    public List<Followings> getFollowingsFromClient() {
        return followingRepository.findAll();
    }

    public Map<String, Followers> getFollowersByMap() {
        return getFollowersFromClient().stream()
                                       .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));
    }

    public Map<String, Followings> getFollowingsByMap() {
        return getFollowingsFromClient().stream()
                                        .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));
    }

    @Transactional
    public void saveFollowers(Map<String, Followers> originalFollowers, List<MemberDto> newFollowers, List<Neighbor> neighbors) {
        createFollowerList(newFollowers, neighbors).stream()
                                                   .filter(followers -> !originalFollowers.containsKey(followers.getGithubLogin()))
                                                   .forEach(followers -> followerRepository.save(followers));
    }

    private List<Followers> createFollowerList(List<MemberDto> newFollowers, List<Neighbor> neighbors) {
        List<Followers> list = new ArrayList<>();
        newFollowers.stream()
                    .map(MemberDto::toFollowers)
                    .forEach(f -> list.add(f));

        neighbors.stream()
                 .forEach(n -> {
                     list.add(new Followers(n.getGithubLogin(), n.getUrl()));
                 });
        return list;
    }

    @Transactional
    public void saveFollowings(Map<String, Followings> originalFollowings, List<MemberDto> newFollowings, List<Neighbor> neighbors) {
        createFollowingList(newFollowings, neighbors).stream()
                                                     .filter(followings -> !originalFollowings.containsKey(followings.getGithubLogin()))
                                                     .forEach(followings -> followingRepository.save(followings));
    }

    private List<Followings> createFollowingList(List<MemberDto> newFollowings, List<Neighbor> neighbors) {
        List<Followings> list = new ArrayList<>();
        newFollowings.stream()
                     .map(MemberDto::toFollowings)
                     .forEach(f -> list.add(f));

        neighbors.stream()
                 .forEach(n -> {
                     list.add(new Followings(n.getGithubLogin(), n.getUrl()));
                 });
        return list;
    }

    public List<Followers> getFollowersFromClient(String handle) {
        log.info("[Feign] github api로부터 {}의 follower 정보를 가져옵니다.", handle);
        List<Followers> followers = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followers> collect = body.stream()
                                          .map(dto -> new Followers(dto.getLogin(), dto.getHtml_url()))
                                          .collect(Collectors.toList());
            followers.addAll(collect);
        }
        return followers;
    }

    public List<Followings> getFollowingsFromClient(String handle) {
        log.info("[Feign] github api로부터 {}의 following 정보를 가져옵니다.", handle);
        List<Followings> followings = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followings> collect = body.stream()
                                           .map(dto -> new Followings(dto.getLogin(), dto.getHtml_url()))
                                           .collect(Collectors.toList());
            followings.addAll(collect);
        }
        return followings;
    }

    public ResponseDto checkFollow(String handle) {
        ResponseDto responseDto = ResponseDto.create();

        Map<String, Followers> followerMap = getFollowersFromClient(handle).stream()
                                                                           .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));

        Map<String, Followings> followingMap = getFollowingsFromClient(handle).stream()
                                                                              .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));

        Map<String, BaseEntity> eachNeighborMap = new HashMap<>();

        responseDto.getNeighbors().create(
                followerMap.entrySet()
                           .stream()
                           .filter(e -> followingMap.containsKey(e.getKey()))
                           .map(e -> {
                               eachNeighborMap.put(e.getKey(), e.getValue());
                               return e.getValue();
                           }).map(f -> new MemberDto(f.getGithubLogin(), f.getUrl()))
                           .collect(Collectors.toList()));

        responseDto.getOnlyFollowers().create(followerMap.entrySet()
                                                         .stream()
                                                         .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                         .map(e -> e.getValue())
                                                         .map(followers -> MemberDto.from(followers.getGithubLogin(), followers.getUrl()))
                                                         .collect(Collectors.toList()));

        responseDto.getOnlyFollowings().create(followingMap.entrySet()
                                                           .stream()
                                                           .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                           .map(e -> e.getValue())
                                                           .map(followers -> MemberDto.from(followers.getGithubLogin(), followers.getUrl()))
                                                           .collect(Collectors.toList()));

        return responseDto;
    }
}
