package me.dolphago.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import me.dolphago.dto.FeignResponseDto;

@FeignClient(
        name = "github-follow",
        url = "https://api.github.com/users")
public interface GithubFeignClient {

    @GetMapping(value = "/{handle}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> getUserInfo(@PathVariable("handle") String handle);

    @GetMapping(value = "/{handle}/following?per_page=100",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<List<FeignResponseDto>> getFollowings(@PathVariable("handle") String handle, @RequestParam("page") int pageNum);

    @GetMapping(value = "/{handle}/followers?per_page=100",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<List<FeignResponseDto>> getFollowers(@PathVariable("handle") String handle, @RequestParam("page") int pageNum);

    @GetMapping(value = "/{handle}/following/{target}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> checkFollow(@PathVariable("handle") String handle, @PathVariable("target") String target);

    @GetMapping(value = "/errorDecoder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> testErrorDecoder(String customHeader);

}
