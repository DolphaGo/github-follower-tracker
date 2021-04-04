package me.dolphago.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.BaseClass;
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
    public Step checkFollowStep(@Value("#{jobParameters[handle]}") String handle) {
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
                                                                           .map(memberDto -> MemberDto.toEntity(memberDto, Neighbor.class))
                                                                           .collect(Collectors.toList());

                                     // BaseClass <-
                                     // followersByMap : <String, Follower> (only) 상대방이 팔로우하고 있어, 나는 그사람 안하고..
                                     // followingsByMap: <String, Following> (only) 나만 그 사람을 팔로우하고있어.
                                     followTrackingService.saveFollowers(followersByMap, responseDto.getOnlyFollowers().getList(), neighbors);
                                     followTrackingService.saveFollowings(followingsByMap, responseDto.getOnlyFollowings().getList(), neighbors);
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }

    private List<ChangeData> getChangeStatus(ResponseDto responseDto, Map<String, Followers> followersByMap, Map<String, Followings> followingsByMap) {
        List<ChangeData> changeData = new ArrayList<>();
        changeData.addAll(getChange(responseDto.getOnlyFollowers().getList(), followersByMap, Relation.NEW_FOLLOWER, Relation.NEW_UNFOLLOWER));
        changeData.addAll(getChange(responseDto.getOnlyFollowings().getList(), followingsByMap, Relation.NEW_FOLLOWING, Relation.NEW_UNFOLLOWING));
        return changeData;
    }

    private <T extends BaseClass> List<ChangeData> getChange(List<MemberDto> currentList, Map<String, T> originalMap, Relation newValue, Relation oldValue) {
        List<ChangeData> changeData = new ArrayList<>();
        for (MemberDto memberDto : currentList) {
            String login = memberDto.getGithubLogin();
            if (!originalMap.containsKey(login)) { // 기존에 없었는데 새로 생긴 사람들
                changeData.add(ChangeData.builder()
                                         .login(login)
                                         .url(memberDto.getUrl())
                                         .status(newValue)
                                         .build());
            } else { originalMap.remove(login); }
        }

        if (!originalMap.isEmpty()) {
            for (Entry<String, T> m : originalMap.entrySet()) { // 기존엔 있었지만, 사라진 사람들
                changeData.add(ChangeData.builder()
                                         .login(m.getKey())
                                         .url(m.getValue().getUrl())
                                         .status(oldValue)
                                         .build());
            }
        }
        return changeData;
    }
}
