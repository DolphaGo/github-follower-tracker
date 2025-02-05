package me.dolphago.service

import io.github.oshai.kotlinlogging.KotlinLogging
import me.dolphago.domain.*
import me.dolphago.dto.FeignResponseDto
import me.dolphago.dto.MemberDto
import me.dolphago.dto.ResponseDto
import me.dolphago.feign.GithubFeignClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

@Transactional(readOnly = true)
@Service
class FollowTrackingService(
    private val client: GithubFeignClient,
    private val followerRepository: FollowerRepository,
    private val followingRepository: FollowingRepository,
    private val historyRepository: HistoryRepository
) {

    private val log = KotlinLogging.logger { }

    val allFollowers: List<Follower>
        get() = followerRepository.findAll()

    val allFollowings: List<Following>
        get() = followingRepository.findAll()

    @Transactional
    fun update(historyList: List<History>) {
        // 새로운 팔로워 저장
        followerRepository.saveAll(getFollowerOf(historyList, Relation.NEW_FOLLOWER))

        // 새로운 언팔로워 삭제
        followerRepository.deleteAll(getFollowerOf(historyList, Relation.NEW_UNFOLLOWER))

        // 새로운 팔로잉 저장
        followingRepository.saveAll(getFollowingOf(historyList, Relation.NEW_FOLLOWING))

        // 새로운 언팔로잉 삭제
        followingRepository.deleteAll(getFollowingOf(historyList, Relation.NEW_UNFOLLOWING))

        // 히스토리 저장
        historyRepository.saveAll(historyList)
    }

    private fun getFollowerOf(historyList: List<History>, followerRelation: Relation): List<Follower?> {
        return historyList.stream()
            .filter { history: History -> history.relation == followerRelation }
            .map { history: History ->
                when (followerRelation) {
                    Relation.NEW_FOLLOWER -> return@map Follower(
                        githubLogin = history.githubLogin,
                        url = history.url
                    )

                    Relation.NEW_UNFOLLOWER -> return@map followerRepository.findByGithubLogin(history.githubLogin)
                    else -> return@map null
                }
            }
            .filter { obj: Follower? -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
    }

    private fun getFollowingOf(historyList: List<History>, followingRelation: Relation): List<Following?> {
        return historyList.stream()
            .filter { history: History -> history.relation == followingRelation }
            .map { history: History ->
                when (followingRelation) {
                    Relation.NEW_FOLLOWING -> return@map Following(
                        githubLogin = history.githubLogin,
                        url = history.url
                    )

                    Relation.NEW_UNFOLLOWING -> return@map followingRepository.findByGithubLogin(history.githubLogin)
                    else -> return@map null
                }
            }
            .filter { obj: Following? -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
    }

    fun getAllFollowers(handle: String): List<Follower> {
        log.info { "[Feign] github api로부터 ${handle}의 follower 정보를 가져옵니다." }
        val followers: MutableList<Follower> = ArrayList()
        var pageNum = 1
        while (true) {
            val body = client.getFollowers(handle, pageNum).body
            if (body!!.isEmpty()) {
                break
            }
            val collect = body.stream()
                .map { dto: FeignResponseDto ->
                    Follower(
                        githubLogin = dto.login,
                        url = dto.html_url
                    )
                }
                .collect(Collectors.toList())
            followers.addAll(collect)
            pageNum++
        }
        return followers
    }

    fun getAllFollowings(handle: String?): List<Following> {
        log.info { "[Feign] github api로부터 ${handle}의 following 정보를 가져옵니다." }
        val followings: MutableList<Following> = ArrayList()
        var pageNum = 1
        while (true) {
            val body = client.getFollowings(handle, pageNum).body
            if (body!!.isEmpty()) {
                break
            }
            val collect = body.stream()
                .map { dto: FeignResponseDto ->
                    Following(
                        githubLogin = dto.login,
                        url = dto.html_url
                    )
                }
                .collect(Collectors.toList())
            followings.addAll(collect)
            pageNum++
        }
        return followings
    }

    fun checkFollow(handle: String): ResponseDto {
        val responseDto = ResponseDto.create()

        val followerMap = getAllFollowers(handle).stream()
            .collect(
                Collectors.toMap(
                    { obj: Follower -> obj.githubLogin }, Function.identity()
                )
            )

        val followingMap = getAllFollowings(handle).stream()
            .collect(
                Collectors.toMap(
                    { obj: Following -> obj.githubLogin }, Function.identity()
                )
            )

        val eachNeighborMap: MutableMap<String, BaseEntity> = HashMap()

        responseDto.neighbors.set(
            followerMap.entries
                .stream()
                .filter { e: Map.Entry<String, Follower> -> followingMap.containsKey(e.key) }
                .map { e: Map.Entry<String, Follower> ->
                    eachNeighborMap[e.key] = e.value
                    e.value
                }.map { f: Follower -> MemberDto(f.githubLogin, f.url) }
                .collect(Collectors.toList()))

        responseDto.onlyFollowers.set(
            followerMap.entries
                .stream()
                .filter { e: Map.Entry<String, Follower> -> !eachNeighborMap.containsKey(e.key) }
                .map { e: Map.Entry<String, Follower> -> e.value }
                .map { followers: Follower -> MemberDto.from(followers.githubLogin, followers.url) }
                .collect(Collectors.toList()))

        responseDto.onlyFollowings.set(
            followingMap.entries
                .stream()
                .filter { e: Map.Entry<String, Following> -> !eachNeighborMap.containsKey(e.key) }
                .map { e: Map.Entry<String, Following> -> e.value }
                .map { followers: Following -> MemberDto.from(followers.githubLogin, followers.url) }
                .collect(Collectors.toList()))

        return responseDto
    }
}
