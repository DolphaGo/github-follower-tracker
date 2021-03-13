package me.dolphago.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.feign.MyFeignClient;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FollowController {
    private final MyFeignClient client;

    @GetMapping("/{handle}")
    public ResponseEntity<?> test(@PathVariable("handle") String handle) {
        log.info("유저 {}의 정보를 가져옵니다.", handle);
        return ResponseEntity.ok(Optional.ofNullable(client.getUser(handle).getBody()));
    }

    @GetMapping("/start")
    public ResponseEntity<?> startTrace(@RequestParam String username) {
        log.info("{}이 팔로우 Tracking을 시작합니다.", username);
        return ResponseEntity.ok(username);
    }
}
