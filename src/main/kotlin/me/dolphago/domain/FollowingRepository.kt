package me.dolphago.domain

import org.springframework.data.jpa.repository.JpaRepository

interface FollowingRepository : JpaRepository<Following, Long> {
    // when I tested, githubId is not unique(not unique values often appeared), it is unique 'github_loginId'
    fun findByGithubLogin(githubLogin: String): Following
}
