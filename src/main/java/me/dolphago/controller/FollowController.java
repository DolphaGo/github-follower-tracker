package me.dolphago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.service.FollowTrackingService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FollowController {
    private final FollowTrackingService followTrackingService;

    @GetMapping("/{handle}")
    public ResponseEntity<?> test(@PathVariable("handle") String handle) {
        log.info("유저 {}의 정보를 가져옵니다.", handle);
        return ResponseEntity.ok(followTrackingService.getUser(handle));
    }

    @GetMapping("/followings/{handle}")
    public ResponseEntity<?> getFollowings(@PathVariable("handle") String handle) {
        log.info("유저 {}의 팔로잉 정보를 저장합니다.", handle);
        return ResponseEntity.ok(followTrackingService.saveFollowings(handle) + "명 저장되었습니다.");
    }

    @GetMapping("/followers/{handle}")
    public ResponseEntity<?> getFollowers(@PathVariable("handle") String handle) throws JsonProcessingException {
        log.info("유저 {}의 팔로워 정보를 저장합니다.", handle);
        return ResponseEntity.ok(followTrackingService.saveFollowers(handle) + "명 저장되었습니다.");
    }

    @GetMapping("/check/{handle}")
    public ResponseEntity<String> check(@PathVariable("handle") String handle) {
        log.info("유저 {}의 팔로우 상태를 점검합니다.", handle);
        log.info("내가 팔로잉하고 있는 {}명 저장", followTrackingService.saveFollowings(handle));
        log.info("나를 팔로우하고 있는 {}명 저장", followTrackingService.saveFollowers(handle));
        return ResponseEntity.ok(followTrackingService.checkFollow(handle));
    }

}
