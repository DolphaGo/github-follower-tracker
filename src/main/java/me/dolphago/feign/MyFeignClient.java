package me.dolphago.feign;

import static me.dolphago.config.DemoConstant.CUSTOM_HEADER_NAME;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import me.dolphago.dto.ResponseDto;

@FeignClient(
        name = "github-follow",
        url = "${feign.api.url}",
        configuration = FeignConfig.class)
public interface MyFeignClient {

    @GetMapping(value = "/users/{handle}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = "application/vnd.github.v3+json")
    ResponseEntity<?> getUser(@PathVariable("handle") String handle);

    @GetMapping(value = "/following",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDto> getFollowings(@RequestHeader(CUSTOM_HEADER_NAME) String customHeader);

    @GetMapping(value = "/follower",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDto> getFollowers(@RequestHeader(CUSTOM_HEADER_NAME) String customHeader);

    @GetMapping(value = "/errorDecoder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDto> testErrorDecoder(@RequestHeader(CUSTOM_HEADER_NAME) String customHeader);

}
