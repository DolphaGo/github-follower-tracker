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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FollowTrackingConfiguration {
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
        return stepBuilderFactory.get("check......")
                                 .tasklet((contribution, chunkContext) -> {
                                     contribution.setExitStatus(ExitStatus.COMPLETED);
                                     log.info("{}의 팔로우 상황을 체크합니다...", handle);
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }
}
