package me.dolphago.job

import io.github.oshai.kotlinlogging.KotlinLogging
import me.dolphago.domain.Follower
import me.dolphago.domain.Following
import me.dolphago.domain.History
import me.dolphago.domain.Relation
import me.dolphago.dto.MemberDto
import me.dolphago.dto.ResponseDto
import me.dolphago.service.FollowTrackingService
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

@Configuration
class CheckFollowConfiguration(
    private val followTrackingService: FollowTrackingService,
    private val transactionManager: PlatformTransactionManager
) {
    val log = KotlinLogging.logger {}

    @Bean
    fun checkFollowJob(jobRepository: JobRepository): Job {
        return JobBuilder("checkFollowJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(checkFollowStep(null, "DolphaGo", jobRepository))
            .build()
    }

    @Bean
    @JobScope
    fun checkFollowStep(
        @Value("#{jobParameters[date]}") date: String?,
        @Value("#{jobParameters[handle] ?: 'DolphaGo'}") handle: String,
        jobRepository: JobRepository
    ): Step {
        log.info { "============= $date ===============" }
        return StepBuilder("checkFollowStep", jobRepository)
            .tasklet(tracking(handle), transactionManager)
            .build()
    }

    fun tracking(handle: String): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            val responseDto =
                followTrackingService.checkFollow(handle) // each, only-follower, only-following from API
            log.info { "###################### From API(Recent status) ######################" }
            log.info { "$responseDto" }

            log.info { "###################### From DB ##########################" }
            val allFollowers = followTrackingService.allFollowers
            val allFollowings = followTrackingService.allFollowings

            log.info { "#################### Follower = ${allFollowers.size} #######################" }
            log.info { "$allFollowers" }

            log.info { "#################### Following = ${allFollowings.size} #######################" }
            log.info { "$allFollowings" }

            followTrackingService.update(createChangedStatusList(responseDto, allFollowers, allFollowings))
            RepeatStatus.FINISHED
        }
    }

    private fun createChangedStatusList(
        responseDto: ResponseDto,
        allFollowers: List<Follower>,
        allFollowings: List<Following>
    ): List<History> {
        val list: MutableList<History> = ArrayList()
        followerCheck(responseDto, allFollowers, list)
        followingCheck(responseDto, allFollowings, list)

        log.info { "#################### Changed List = ${list.size} #######################" }
        log.info { "$list" }
        return list
    }

    private fun followingCheck(responseDto: ResponseDto, allFollowings: List<Following>, list: MutableList<History>) {
        // 현재 내가 팔로잉하고 있는 사람 = 나만 상대방을 팔로우 하고 있는 사람들 + 서로 이웃인 사람들
        val currentFollowings = getCurrentFollowings(responseDto)

        // 기존 팔로우를 하고 있는 사람들 조회
        for (following in allFollowings) {
            // 기존 Following, 현재도 Following => 변동 없음
            if (currentFollowings.containsKey(following.githubLogin)) {
                currentFollowings.remove(following.githubLogin)
                continue
            }

            // 기존 Following, 현재는 unFollowing => NEW_UNFOLLOWING
            if (!currentFollowings.containsKey(following.githubLogin)) {
                list.add(History(following.githubLogin, following.url, Relation.NEW_UNFOLLOWING))
            }
        }

        // 기존 Following으로 부터 지워지지 않은 현재 Followings => NEW_FOLLOWING
        currentFollowings.values
            .stream()
            .map { memberDto: MemberDto -> History(memberDto.githubLogin, memberDto.url, Relation.NEW_FOLLOWING) }
            .forEach { e: History -> list.add(e) }
    }

    private fun followerCheck(responseDto: ResponseDto, allFollowers: List<Follower>, list: MutableList<History>) {
        // 현재 나를 팔로우 하고 있는 사람 = 상대방만 나를 팔로우 하고 있는 사람들 + 서로 이웃인 사람들
        val currentFollowers = getCurrentFollowers(responseDto)

        for (follower in allFollowers) {
            // 기존 Follower, 현재도 Follower => 변동 없음
            if (currentFollowers.containsKey(follower.githubLogin)) {
                currentFollowers.remove(follower.githubLogin)
                continue
            }

            // 기존 Follower, 현재는 unFollower => NEW_UNFOLLOWER
            if (!currentFollowers.containsKey(follower.githubLogin)) {
                list.add(History(follower.githubLogin, follower.url, Relation.NEW_UNFOLLOWER))
            }
        }

        // 기존 Follower으로 부터 지워지지 않은 현재 Follower => NEW_FOLLOWER
        currentFollowers.values
            .stream()
            .map { memberDto: MemberDto -> History(memberDto.githubLogin, memberDto.url, Relation.NEW_FOLLOWER) }
            .forEach { e: History -> list.add(e) }
    }

    private fun getCurrentFollowings(responseDto: ResponseDto): MutableMap<String, MemberDto> {
        val currentFollowings = ArrayList<MemberDto>()
        currentFollowings.addAll(responseDto.onlyFollowings.list)
        currentFollowings.addAll(responseDto.neighbors.list)
        return currentFollowings.stream().collect(
            Collectors.toMap(
                { obj: MemberDto -> obj.githubLogin }, Function.identity()
            )
        )
    }

    private fun getCurrentFollowers(responseDto: ResponseDto): MutableMap<String, MemberDto> {
        val currentFollowers = ArrayList<MemberDto>()
        currentFollowers.addAll(responseDto.onlyFollowers.list)
        currentFollowers.addAll(responseDto.neighbors.list)
        return currentFollowers.stream().collect(
            Collectors.toMap(
                { obj: MemberDto -> obj.githubLogin }, Function.identity()
            )
        )
    }

    private fun updateFollowingList(responseDto: ResponseDto): List<Following> {
        val list: MutableList<Following> = ArrayList()
        responseDto.onlyFollowings.list.forEach(Consumer { memberDto: MemberDto ->
            list.add(
                MemberDto.toFollowings(
                    memberDto
                )
            )
        })
        responseDto.neighbors.list.forEach(Consumer { n: MemberDto ->
            list.add(
                Following(
                    githubLogin = n.githubLogin,
                    url = n.url
                )
            )
        })
        return list
    }

    private fun updateFollowerList(responseDto: ResponseDto): List<Follower> {
        val list: MutableList<Follower> = ArrayList()
        responseDto.onlyFollowers.list.forEach(Consumer { memberDto: MemberDto ->
            list.add(
                MemberDto.toFollowers(
                    memberDto
                )
            )
        })
        responseDto.neighbors.list.forEach(Consumer { n: MemberDto ->
            list.add(
                Follower(
                    githubLogin = n.githubLogin,
                    url = n.url
                )
            )
        })
        return list
    }
}
