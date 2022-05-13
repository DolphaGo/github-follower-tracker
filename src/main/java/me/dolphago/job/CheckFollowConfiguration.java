package me.dolphago.job;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.History;
import me.dolphago.domain.Follower;
import me.dolphago.domain.Following;
import me.dolphago.domain.Relation;
import me.dolphago.dto.MemberDto;
import me.dolphago.dto.ResponseDto;
import me.dolphago.service.FollowTrackingService;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CheckFollowConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final FollowTrackingService followTrackingService;

    @Bean
    public Job checkFollowJob() {
        return jobBuilderFactory.get("checkFollowJob")
                                .incrementer(new RunIdIncrementer())
                                .start(checkFollowStep(null, null))
                                .build();
    }

    @Bean
    @JobScope
    public Step checkFollowStep(@Value("#{jobParameters[date]}") String date, @Value("#{jobParameters[handle] ?: 'DolphaGo'}") String handle) {
        log.info("============= {} ===============", date);
        return stepBuilderFactory.get("checkFollowStep")
                                 .tasklet(tracking(handle))
                                 .build();
    }

    public Tasklet tracking(final String handle) {
        return (contribution, chunkContext) -> {
            ResponseDto responseDto = followTrackingService.checkFollow(handle); // each, only-follower, only-following from API

            log.info("###################### From API(Recent status) ######################");
            log.info("{}", responseDto);

            log.info("###################### From DB ##########################");
            final List<Follower> allFollowers = followTrackingService.getAllFollowers();
            final List<Following> allFollowings = followTrackingService.getAllFollowings();

            log.info("#################### Follower = {} #######################", allFollowers.size());
            log.info("{}", allFollowers);

            log.info("#################### Following = {} #######################", allFollowings.size());
            log.info("{}", allFollowings);

            followTrackingService.update(createChangedStatusList(responseDto, allFollowers, allFollowings));
            return RepeatStatus.FINISHED;
        };
    }

    private List<History> createChangedStatusList(final ResponseDto responseDto, final List<Follower> allFollowers, final List<Following> allFollowings) {
        List<History> list = new ArrayList<>();
        followerCheck(responseDto, allFollowers, list);
        followingCheck(responseDto, allFollowings, list);

        log.info("#################### Changed List = {} #######################", list.size());
        log.info("{}", list);
        return list;
    }

    private void followingCheck(final ResponseDto responseDto, final List<Following> allFollowings, final List<History> list) {
        // 현재 내가 팔로잉하고 있는 사람 = 나만 상대방을 팔로우 하고 있는 사람들 + 서로 이웃인 사람들
        final Map<String, MemberDto> currentFollowings = getCurrentFollowings(responseDto);

        // 기존 팔로우를 하고 있는 사람들 조회
        for (Following following : allFollowings) {
            // 기존 Following, 현재도 Following => 변동 없음
            if (currentFollowings.containsKey(following.getGithubLogin())) {
                currentFollowings.remove(following.getGithubLogin());
                continue;
            }

            // 기존 Following, 현재는 unFollowing => NEW_UNFOLLOWING
            if (!currentFollowings.containsKey(following.getGithubLogin())) {
                list.add(new History(following.getGithubLogin(), following.getUrl(), Relation.NEW_UNFOLLOWING));
            }
        }

        // 기존 Following으로 부터 지워지지 않은 현재 Followings => NEW_FOLLOWING
        currentFollowings.values()
                         .stream().map(memberDto -> new History(memberDto.getGithubLogin(), memberDto.getUrl(), Relation.NEW_FOLLOWING))
                         .forEach(list::add);
    }

    private void followerCheck(final ResponseDto responseDto, final List<Follower> allFollowers, final List<History> list) {
        // 현재 나를 팔로우 하고 있는 사람 = 상대방만 나를 팔로우 하고 있는 사람들 + 서로 이웃인 사람들
        final Map<String, MemberDto> currentFollowers = getCurrentFollowers(responseDto);

        for (Follower follower : allFollowers) {
            // 기존 Follower, 현재도 Follower => 변동 없음
            if (currentFollowers.containsKey(follower.getGithubLogin())) {
                currentFollowers.remove(follower.getGithubLogin());
                continue;
            }

            // 기존 Follower, 현재는 unFollower => NEW_UNFOLLOWER
            if (!currentFollowers.containsKey(follower.getGithubLogin())) {
                list.add(new History(follower.getGithubLogin(), follower.getUrl(), Relation.NEW_UNFOLLOWER));
            }
        }

        // 기존 Follower으로 부터 지워지지 않은 현재 Follower => NEW_FOLLOWER
        currentFollowers.values()
                        .stream().map(memberDto -> new History(memberDto.getGithubLogin(), memberDto.getUrl(), Relation.NEW_FOLLOWER))
                        .forEach(list::add);
    }

    private Map<String, MemberDto> getCurrentFollowings(final ResponseDto responseDto) {
        final ArrayList<MemberDto> currentFollowings = new ArrayList<>();
        currentFollowings.addAll(responseDto.getOnlyFollowings().getList());
        currentFollowings.addAll(responseDto.getNeighbors().getList());
        return currentFollowings.stream().collect(toMap(MemberDto::getGithubLogin, Function.identity()));
    }

    private Map<String, MemberDto> getCurrentFollowers(final ResponseDto responseDto) {
        final ArrayList<MemberDto> currentFollowers = new ArrayList<>();
        currentFollowers.addAll(responseDto.getOnlyFollowers().getList());
        currentFollowers.addAll(responseDto.getNeighbors().getList());
        return currentFollowers.stream().collect(toMap(MemberDto::getGithubLogin, Function.identity()));
    }

    private List<Following> updateFollowingList(ResponseDto responseDto) {
        List<Following> list = new ArrayList<>();
        responseDto.getOnlyFollowings().getList().forEach(memberDto -> list.add(MemberDto.toFollowings(memberDto)));
        responseDto.getNeighbors().getList().forEach(n -> list.add(new Following(n.getGithubLogin(), n.getUrl())));
        return list;
    }

    private List<Follower> updateFollowerList(ResponseDto responseDto) {
        List<Follower> list = new ArrayList<>();
        responseDto.getOnlyFollowers().getList().forEach(memberDto -> list.add(MemberDto.toFollowers(memberDto)));
        responseDto.getNeighbors().getList().forEach(n -> list.add(new Follower(n.getGithubLogin(), n.getUrl())));
        return list;
    }
}
