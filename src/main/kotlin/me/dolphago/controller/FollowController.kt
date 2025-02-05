package me.dolphago.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import me.dolphago.dto.ResponseDto
import me.dolphago.service.FollowTrackingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FollowController(
    val followTrackingService: FollowTrackingService
) {
    val log = KotlinLogging.logger { }

    @GetMapping("/check")
    fun check(@RequestParam("handle") handle: String): ResponseEntity<ResponseDto> {
        log.info { "Check Follow ${handle}'s Status....." }
        return ResponseEntity.ok(followTrackingService.checkFollow(handle))
    }
}
