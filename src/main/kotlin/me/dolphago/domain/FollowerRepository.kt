package me.dolphago.domain

import org.springframework.data.jpa.repository.JpaRepository

interface FollowerRepository : JpaRepository<Follower, Long> {
    fun findByGithubLogin(githubLogin: String): Follower
}
