package me.dolphago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
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
            List<ResponseDto> body = client.getFollowers(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            count += body.size();
            for (ResponseDto dto : body) {
                Optional<Followers> byGithubId = followerRepository.findByGithubId(dto.getId());
                if (byGithubId.isPresent()) { continue; }
                Followers follower = Followers.builder()
                                              .githubId(dto.getId())
                                              .githubLogin(dto.getLogin())
                                              .build();
                try {
                    followerRepository.save(follower);
                } catch (Exception e) {
                    log.warn("중복된 아이디: {}", dto.getLogin());
                }
            }
        }
        return count;
    }

    @Transactional
    public long saveFollowings(String handle) {
        int count = 0;
        for (int pageNum = 1; ; pageNum++) {
            List<ResponseDto> body = client.getFollowings(handle, pageNum).getBody();
            if (body.isEmpty()) { break; }
            count += body.size();
            for (ResponseDto dto : body) {
                Optional<Followings> byGithubId = followingRepository.findByGithubId(dto.getId());
                if (byGithubId.isPresent()) { continue; }
                Followings following = Followings.builder()
                                                 .githubId(dto.getId())
                                                 .githubLogin(dto.getLogin())
                                                 .build();

                try {
                    followingRepository.save(following);
                } catch (Exception e) {
                    log.warn("중복된 아이디: {}", dto.getLogin());
                }
            }
        }
        return count;
    }

    public Optional<?> getUser(String handle) {
        return Optional.ofNullable(client.getUserInfo(handle).getBody());
    }
}
