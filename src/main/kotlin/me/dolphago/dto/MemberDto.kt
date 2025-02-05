package me.dolphago.dto

import me.dolphago.domain.Follower
import me.dolphago.domain.Following

data class MemberDto(
    val githubLogin: String,
    val url: String
) {
    companion object {
        fun from(githubLogin: String, url: String): MemberDto {
            return MemberDto(githubLogin, url)
        }

        fun toFollowers(memberDto: MemberDto): Follower {
            return Follower(githubLogin = memberDto.githubLogin, url = memberDto.url)
        }

        fun toFollowings(memberDto: MemberDto): Following {
            return Following(githubLogin = memberDto.githubLogin, url = memberDto.url)
        }
    }
}
