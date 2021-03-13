package me.dolphago.feign;

import org.springframework.http.HttpStatus;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        final HttpStatus httpStatus = HttpStatus.resolve(response.status());

        // Handle Custom Error Status Code
        // The rest is delegated to the default error decoder
        if (httpStatus == HttpStatus.NOT_FOUND) {
            log.warn("[Slf4j] Http Status = {}", httpStatus);
            throw new RuntimeException(String.format("[RuntimeException] Http Status is %s", httpStatus));
        }

        if (httpStatus == HttpStatus.FORBIDDEN) {
            log.info("Forbidden...... {}, {}", methodKey, response);
        }

        return errorDecoder.decode(methodKey, response);
    }

}
