package me.dolphago.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public FeignErrorDecoder DemoErrorDecoder() {
        return new FeignErrorDecoder();
    }

}