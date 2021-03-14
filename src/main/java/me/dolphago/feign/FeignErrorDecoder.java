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

        if (httpStatus == HttpStatus.NOT_FOUND) {
            throw new RuntimeException("[Message] 그런 사용자는 없네요.");
        }

        if (httpStatus == HttpStatus.FORBIDDEN) {
            log.info("Forbidden...... {}, {}", methodKey, response);
        }

        return errorDecoder.decode(methodKey, response);
    }

}
