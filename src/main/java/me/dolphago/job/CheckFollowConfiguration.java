package me.dolphago.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.ChangeData;
import me.dolphago.domain.Followers;
import me.dolphago.domain.Followings;
import me.dolphago.domain.Neighbor;
import me.dolphago.domain.Relation;
import me.dolphago.dto.MemberDto;
import me.dolphago.dto.ResponseDto;
import me.dolphago.service.ChangeDataService;
import me.dolphago.service.FollowTrackingService;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CheckFollowConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final FollowTrackingService followTrackingService;
    private final ChangeDataService changeDataService;

    @Bean
    public Job checkFollowJob() {
        return jobBuilderFactory.get("checkFollowJob")
                                .start(checkFollowStep(null))
                                .build();
    }

    @Bean
    @JobScope
    public Step checkFollowStep(@Value("#{jobParameters[handle] ?: 'DolphaGo'}") String handle) {
        return stepBuilderFactory.get("checkFollowStep")
                                 .tasklet((contribution, chunkContext) -> {
                                     ResponseDto responseDto = followTrackingService.checkFollow(handle); // each, only-follower, only-following from API

                                     Map<String, Followers> followersByMap = followTrackingService.getFollowersByMap();// DB에서 가져온 follower
                                     Map<String, Followings> followingsByMap = followTrackingService.getFollowingsByMap();// DB에서 가져온 following

                                     List<ChangeData> followerChange = getChangeStatus(responseDto, followersByMap, followingsByMap);
                                     changeDataService.saveAll(followerChange); // 변경 사항만 저장한다.

                                     List<Neighbor> neighbors = responseDto.getNeighbors()
                                                                           .getList()
                                                                           .stream()
                                                                           .map(MemberDto::toNeighbor)
                                                                           .collect(Collectors.toList());

                                     // followersByMap : <String, Follower> (only) 상대방만 나를 팔로우
                                     // followingsByMap: <String, Following> (only) 나만 그 사람을 팔로우
                                     followTrackingService.saveFollowers(followersByMap, responseDto.getOnlyFollowers().getList(), neighbors);
                                     followTrackingService.saveFollowings(followingsByMap, responseDto.getOnlyFollowings().getList(), neighbors);
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }

    private List<ChangeData> getChangeStatus(ResponseDto responseDto, Map<String, Followers> followersByMap, Map<String, Followings> followingsByMap) {
        List<ChangeData> changeData = new ArrayList<>();
        changeData.addAll(getChangeFromFollower(responseDto.getOnlyFollowers().getList(), followersByMap));
        changeData.addAll(getChangeFromFollowing(responseDto.getOnlyFollowings().getList(), followingsByMap));
        return changeData;
    }

    private List<ChangeData> getChangeFromFollower(List<MemberDto> currentList, Map<String, Followers> originalMap) {
        List<ChangeData> changeData = new ArrayList<>();
        for (MemberDto memberDto : currentList) {
            String login = memberDto.getGithubLogin();
            if (!originalMap.containsKey(login)) { // 기존에 없었는데 새로 생긴 사람들
                changeData.add(new ChangeData(login, memberDto.getUrl(), Relation.NEW_FOLLOWER));
            } else { originalMap.remove(login); }
        }

        if (!originalMap.isEmpty()) {
            for (Entry<String, Followers> m : originalMap.entrySet()) { // 기존엔 있었지만, 사라진 사람들
                changeData.add(new ChangeData(m.getKey(), m.getValue().getUrl(), Relation.NEW_UNFOLLOWER));
            }
        }
        return changeData;
    }

    private List<ChangeData> getChangeFromFollowing(List<MemberDto> currentList, Map<String, Followings> originalMap) {
        List<ChangeData> changeData = new ArrayList<>();
        for (MemberDto memberDto : currentList) {
            String login = memberDto.getGithubLogin();
            if (!originalMap.containsKey(login)) { // 기존에 없었는데 새로 생긴 사람들
                changeData.add(new ChangeData(login, memberDto.getUrl(), Relation.NEW_FOLLOWING));
            } else { originalMap.remove(login); }
        }

        if (!originalMap.isEmpty()) {
            for (Entry<String, Followings> m : originalMap.entrySet()) { // 기존엔 있었지만, 사라진 사람들
                changeData.add(new ChangeData(m.getKey(), m.getValue().getUrl(), Relation.NEW_UNFOLLOWING));
            }
        }
        return changeData;
    }
}
