package me.dolphago.feign

import me.dolphago.dto.FeignResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "github-follow", url = "https://api.github.com/users")
interface GithubFeignClient {
    @GetMapping(
        value = ["/{handle}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = ["application/vnd.github.v3+json"]
    )
    fun getUserInfo(@PathVariable("handle") handle: String): ResponseEntity<*>

    @GetMapping(
        value = ["/{handle}/following?per_page=100"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = ["application/vnd.github.v3+json"]
    )
    fun getFollowings(
        @PathVariable("handle") handle: String?,
        @RequestParam("page") pageNum: Int
    ): ResponseEntity<List<FeignResponseDto>>

    @GetMapping(
        value = ["/{handle}/followers?per_page=100"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = ["application/vnd.github.v3+json"]
    )
    fun getFollowers(
        @PathVariable("handle") handle: String,
        @RequestParam("page") pageNum: Int
    ): ResponseEntity<List<FeignResponseDto>>

    @GetMapping(
        value = ["/{handle}/following/{target}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = ["application/vnd.github.v3+json"]
    )
    fun checkFollow(
        @PathVariable("handle") handle: String,
        @PathVariable("target") target: String
    ): ResponseEntity<*>

    @GetMapping(
        value = ["/errorDecoder"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun testErrorDecoder(customHeader: String): ResponseEntity<*>
}
