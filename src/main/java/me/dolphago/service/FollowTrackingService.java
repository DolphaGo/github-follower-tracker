package me.dolphago.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.BaseClass;
import me.dolphago.domain.ChangeData;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.Followers;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.domain.Followings;
import me.dolphago.domain.Neighbor;
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

    public List<Followers> getFollowersFromClient() {
        return followerRepository.findAll();
    }

    public List<Followings> getFollowingsFromClient() {
        return followingRepository.findAll();
    }

    public Map<String, Followers> getFollowersByMap() {
        BaseClass.of()
        return getFollowersFromClient().stream()
                                       .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));
    }

    public Map<String, Followings> getFollowingsByMap() {
        return getFollowingsFromClient().stream()
                                        .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));
    }

    @Transactional
    public void saveFollowers(Map<String, Followers> originalFollowers, List<MemberDto> newFollowers, List<Neighbor> neighbors) {
        List<Followers> followersList = create(newFollowers, neighbors, Followers.class);
        for (Followers follower : followersList) {
            if (!originalFollowers.containsKey(follower.getGithubLogin())) {
                followerRepository.save(follower);
            }
        }
    }

    private <T extends BaseClass> List<T> create(List<MemberDto> memberDtos, List<Neighbor> neighbors, Class<T> cls) {
        List<T> list = new ArrayList<>();
        for (MemberDto memberDto : memberDtos) {
            T t = MemberDto.toEntity(memberDto, cls);
            list.add(t);
        }

        for (Neighbor neighbor : neighbors) {
            T convert = convert(neighbor, cls);
            list.add(convert);
        }
        return list;
    }

    private <T extends BaseClass> T convert(BaseClass neighbor, Class<T> cls) {
        return (T) T.builder()
                    .url(neighbor.getUrl())
                    .githubLogin(neighbor.getGithubLogin())
                    .build();

    }

    @Transactional
    public <T> void saveFollowings(Map<String, Followings> originalFollowings, List<MemberDto> newFollowings, List<Neighbor> neighbors) {
        List<Followings> followingList = create(newFollowings, neighbors, Followings.class);
        for (Followings following : followingList) {
            if (!originalFollowings.containsKey(following.getGithubLogin())) {
                followingRepository.save(following);
            }
        }
    }

    public Optional<?> getUser(String handle) {
        return Optional.ofNullable(client.getUserInfo(handle).getBody());
    }

    public List<Followers> getFollowersFromClient(String handle) {
        log.info("[Feign] github api로부터 {}의 follower 정보를 가져옵니다.", handle);
        List<Followers> followers = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followers> collect = body.stream()
                                          .map(dto -> Followers.builder()
                                                               .githubLogin(dto.getLogin())
                                                               .url(dto.getHtml_url())
                                                               .build())
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
                                           .map(dto -> Followings.builder()
                                                                 .githubLogin(dto.getLogin())
                                                                 .url(dto.getHtml_url())
                                                                 .build())
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

        Map<String, BaseClass> eachNeighborMap = new HashMap<>();

        responseDto.getNeighbors().create(
                followerMap.entrySet()
                           .stream()
                           .filter(e -> followingMap.containsKey(e.getKey()))
                           .map(e -> {
                               eachNeighborMap.put(e.getKey(), e.getValue());
                               return e.getValue();
                           }).map(MemberDto::from)
                           .collect(Collectors.toList()));

        responseDto.getOnlyFollowers().create(followerMap.entrySet()
                                                         .stream()
                                                         .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                         .map(e -> e.getValue())
                                                         .map(MemberDto::from)
                                                         .collect(Collectors.toList()));

        responseDto.getOnlyFollowings().create(followingMap.entrySet()
                                                           .stream()
                                                           .filter(e -> !eachNeighborMap.containsKey(e.getKey()))
                                                           .map(e -> e.getValue())
                                                           .map(MemberDto::from)
                                                           .collect(Collectors.toList()));

        return responseDto;
    }
}
