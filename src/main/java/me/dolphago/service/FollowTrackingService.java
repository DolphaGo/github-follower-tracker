package me.dolphago.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.Followers;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.domain.Followings;
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
            final List<ResponseDto> body = client.getFollowers(handle, pageNum).getBody();
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
            final List<ResponseDto> body = client.getFollowings(handle, pageNum).getBody();
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
            final List<ResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followers> collect = body.stream()
                                          .map(dto -> Followers.builder()
                                                               .githubId(dto.getId())
                                                               .githubLogin(dto.getLogin())
                                                               .build())
                                          .collect(Collectors.toList());
            followers.addAll(collect);
        }
        return followers;
    }

    public List<Followings> getFollowings(String handle) {
        List<Followings> followings = new ArrayList<>();
        for (int pageNum = 1; ; pageNum++) {
            final List<ResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            List<Followings> collect = body.stream()
                                           .map(dto -> Followings.builder()
                                                                 .githubId(dto.getId())
                                                                 .githubLogin(dto.getLogin())
                                                                 .build())
                                           .collect(Collectors.toList());
            followings.addAll(collect);
        }
        return followings;
    }

    public String checkFollow(String handle) {
        List<Followings> followings = getFollowings(handle);
        List<Followers> followers = getFollowers(handle);

        StringBuilder sb = new StringBuilder();
        List<String> eachFollow = new ArrayList<>();

        for (Followers er : followers) {
            for (Followings ing : followings) {
                if (isEqual(er.getGithubLogin(), ing.getGithubLogin())) {
                    eachFollow.add(er.getGithubLogin());
                    break;
                }
            }
        }

        for (String e : eachFollow) {
            followers.removeIf(er -> isEqual(er.getGithubLogin(), e));
        }

        for (String e : eachFollow) {
            followings.removeIf(ing -> isEqual(ing.getGithubLogin(), e));
        }

        sb.append("============ Each other's neighbors ========= : " + eachFollow.size() + "<br/>");
        for (String e : eachFollow) {
            sb.append(e).append("<br/>");
        }

        sb.append("============ Someone who's following you(not you) ========= : " + followers.size() + "<br/>");
        for (Followers f : followers) {
            sb.append(f.getGithubLogin()).append("<br/>");
        }

        sb.append("============  Someone you are following(not the him) ========= : " + followings.size() + "<br/>");
        for (Followings f : followings) {
            sb.append(f.getGithubLogin()).append("<br/>");
        }
        return sb.toString();
    }

    static boolean isEqual(Object a, Object b) {
        return a.equals(b);
    }

}
