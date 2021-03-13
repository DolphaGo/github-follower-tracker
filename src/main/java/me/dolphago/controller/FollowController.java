package me.dolphago.controller;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.feign.GithubFeignClient;
import me.dolphago.util.GithubParser;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FollowController {
    private final GithubFeignClient client;
    private final GithubParser githubParser;

    @GetMapping("/{handle}")
    public ResponseEntity<?> test(@PathVariable("handle") String handle) {
        log.info("유저 {}의 정보를 가져옵니다.", handle);
        return ResponseEntity.ok(Optional.ofNullable(client.getUserInfo(handle).getBody()));
    }

    @GetMapping("/followings/{handle}")
    public ResponseEntity<?> getFollowings(@PathVariable("handle") String handle) {
        log.info("유저 {}의 팔로잉 정보를 가져옵니다.", handle);
        return ResponseEntity.ok(Optional.ofNullable(client.getFollowings(handle).getBody()));
    }

    @GetMapping("/followers/{handle}")
    public ResponseEntity<?> getFollowers(@PathVariable("handle") String handle) throws JsonProcessingException {
        log.info("유저 {}의 팔로워 정보를 가져옵니다.", handle);
        Object o = client.getFollowers(handle).getBody();
        log.info("{}", o);
        return ResponseEntity.ok(githubParser.followerParse(o));
    }
}
