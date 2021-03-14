package me.dolphago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.service.FollowTrackingService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FollowController {
    private final FollowTrackingService followTrackingService;

    @GetMapping("/check/{handle}")
    public ResponseEntity<String> check(@PathVariable("handle") String handle) {
        log.info("유저 {}의 팔로우 상태를 점검합니다.", handle);
        return ResponseEntity.ok(followTrackingService.checkFollow(handle));
    }
}
