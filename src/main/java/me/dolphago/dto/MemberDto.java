package me.dolphago.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.dolphago.domain.Follower;
import me.dolphago.domain.Following;

@ToString
@Getter
@NoArgsConstructor
public class MemberDto {

    private String githubLogin;
    private String url;

    public MemberDto(String githubLogin, String url) {
        this.githubLogin = githubLogin;
        this.url = url;
    }

    public static MemberDto from(String githubLogin, String url) {
        return new MemberDto(githubLogin, url);
    }

    public static Follower toFollowers(MemberDto memberDto) {
        return new Follower(memberDto.getGithubLogin(), memberDto.getUrl());
    }

    public static Following toFollowings(MemberDto memberDto) {
        return new Following(memberDto.getGithubLogin(), memberDto.getUrl());
    }

}
