package me.dolphago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sun.istack.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.dto.ResponseDto;
import me.dolphago.service.FollowTrackingService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class FollowController {
    private final FollowTrackingService followTrackingService;

    @GetMapping("/check")
    public ResponseEntity<ResponseDto> check(@NotNull @RequestParam("handle") String handle) {
        log.info("Check Follow {}'s Status.....", handle);
        return ResponseEntity.ok(followTrackingService.checkFollow(handle));
    }
}
