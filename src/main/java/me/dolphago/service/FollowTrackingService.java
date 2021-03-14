package me.dolphago.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    @Transactional
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

    @Transactional(readOnly = true)
    public String checkFollow(String handle) {
        StringBuilder sb = new StringBuilder();
        List<Followings> followings = followingRepository.findAll(); // 내가 팔로잉하고 있는 사람
        List<Followers> followers = followerRepository.findAll(); // 나를 팔로우하고 있는 사람

        List<String> eachFollow = new ArrayList<>();

        // 서로 이웃
        for (Followers er : followers) {
            for (Followings ing : followings) {
                if (isEqual(er.getId(), ing.getId())) {
                    eachFollow.add(er.getGithubLogin());
                    break;
                }
            }
        }

        // 나는 이 사람들을 팔로우 하고 있지 않음
        for (String each : eachFollow) {
            followers.removeIf(er -> er.getGithubLogin().equals(each));
        }

        // 나는 이 사람들을 팔로우 하고 있음
        for (String each : eachFollow){
            followings.removeIf(ing -> ing.getGithubLogin().equals(each));
        }

        sb.append("============ 서로 이웃 명단 =========").append('\n');
        for(String each : eachFollow){
            sb.append(each).append('\n');
        }

        sb.append("============ 내가 팔로우 하고 있지 않은 사람들 =========").append('\n');
        for(Followers f : followers){
            sb.append(f.getGithubLogin()).append('\n');
        }

        sb.append("============  나를 팔로우 하고 있지 않은 사람들 =========").append('\n');
        for(Followings f :followings){
            sb.append(f.getGithubLogin()).append('\n');
        }

//        for (Followings f : followings) {
//            log.info("{}이 {}를 팔로우 하고 있는지 검사합니다.", f.getGithubLogin(), handle);
//            ResponseEntity<?> responseEntity = client.checkFollow(f.getGithubLogin(), handle);
//            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
//                sb.append(f.getGithubLogin() + "은 나를 팔로우 하고 있지 않습니다.").append('\n');
//                log.info("{} <- 이 사람은 나를 팔로우 하고 있지 않습니다. ", f.getGithubLogin());
//            }
//        }
//
//        for (Followers f : followers) {
//            log.info("{}이 {}를 팔로우 하고 있는지 검사합니다.", handle, f.getGithubLogin());
//            ResponseEntity<?> responseEntity = client.checkFollow(handle, f.getGithubLogin());
//            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
//                sb.append("나는 " + f.getGithubLogin() + "를 팔로우 하고 있지 않습니다.").append('\n');
//                log.info("{} <- 나는 이 사람을 팔로우 하고 있지 않습니다. ", f.getGithubLogin());
//            }
//        }

        return sb.toString();
    }

    static boolean isEqual(Long a, Long b) {
        return a.equals(b);
    }

}
