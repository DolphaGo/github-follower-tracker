package me.dolphago.service;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.FollowerRepository;
import me.dolphago.domain.FollowingRepository;
import me.dolphago.feign.GithubFeignClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FollowTrackingConfiguration {
    private final GithubFeignClient client;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private String INJECT_HANDLE = null;

    @Bean
    public Job checkFollowJob() {
        return jobBuilderFactory.get("followCheck")
                                .start(checkStep(INJECT_HANDLE))
                                .build();
    }

    @JobScope
    @Bean
    public Step checkStep(@Value("#{jobParameters[handle]}") String handle) {
        String checkHandle = handle ==null ? "DolphaGo" : handle;
        return stepBuilderFactory.get("check......")
                                 .tasklet((contribution, chunkContext) -> {
                                     contribution.setExitStatus(ExitStatus.COMPLETED);
                                     log.info("{}의 팔로우 상황을 체크합니다...", checkHandle);
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }
}
