package me.dolphago;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableBatchProcessing
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class GithubFollowerTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubFollowerTrackerApplication.class, args);
    }
}
