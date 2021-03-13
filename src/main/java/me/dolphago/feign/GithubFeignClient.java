package me.dolphago.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "github-follow",
        url = "https://api.github.com/users",
        configuration = FeignConfig.class)
public interface GithubFeignClient {

    @GetMapping(value = "/{handle}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> getUserInfo(@PathVariable("handle") String handle);

    @GetMapping(value = "/{handle}/following",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> getFollowings(@PathVariable("handle") String handle);

    @GetMapping(value = "/{handle}/followers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> getFollowers(@PathVariable("handle") String handle);

    @GetMapping(value = "/errorDecoder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> testErrorDecoder(String customHeader);

}
