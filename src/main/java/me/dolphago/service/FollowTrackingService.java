package me.dolphago.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.BaseEntity;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.Followers;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.domain.Followings;
import me.dolphago.dto.FeignResponseDto;
import me.dolphago.dto.MemberDto;
import me.dolphago.dto.ResponseDto;
import me.dolphago.feign.GithubFeignClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowTrackingService {
    private final GithubFeignClient client;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;

    public int saveFollowers(String handle) {
        int count = 0;
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            count += body.stream()
                         .filter(dto -> {
                             Optional<Followers> result = followerRepository.findByGithubId(dto.getId());
                             return result.isEmpty();
                         }).map(dto -> Followers.builder()
                                                .githubId(dto.getId())
                                                .githubLogin(dto.getLogin())
                                                .build())
                         .map(followerRepository::save)
                         .count();
        }
        return count;
    }

    public long saveFollowings(String handle) {
        int count = 0;
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            count += body.stream()
                         .filter(dto -> {
                             Optional<Followings> result = followingRepository.findByGithubId(dto.getId());
                             return result.isEmpty();
                         }).map(dto -> Followings.builder()
                                                 .githubId(dto.getId())
                                                 .githubLogin(dto.getLogin())
                                                 .build())
                         .map(followingRepository::save)
                         .count();
        }
        return count;
    }

    public Optional<?> getUser(String handle) {
        return Optional.ofNullable(client.getUserInfo(handle).getBody());
    }

    public List<Followers> getFollowers(String handle) {
        List<Followers> followers = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followers> collect = body.stream()
                                          .map(dto -> Followers.builder()
                                                               .githubId(dto.getId())
                                                               .githubLogin(dto.getLogin())
                                                               .url(dto.getHtml_url())
                                                               .build())
                                          .collect(Collectors.toList());
            followers.addAll(collect);
        }
        return followers;
    }

    public List<Followings> getFollowings(String handle) {
        List<Followings> followings = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<FeignResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followings> collect = body.stream()
                                           .map(dto -> Followings.builder()
                                                                 .githubId(dto.getId())
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

        Map<String, Followers> followerMap = getFollowers(handle).stream()
                                                                 .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));

        Map<String, Followings> followingMap = getFollowings(handle).stream()
                                                                    .collect(Collectors.toMap(o -> o.getGithubLogin(), o -> o));

        Map<String, BaseEntity> eachNeighborMap = new HashMap<>();

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
