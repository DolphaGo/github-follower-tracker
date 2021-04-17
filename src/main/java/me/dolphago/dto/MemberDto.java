package me.dolphago.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.dolphago.domain.BaseEntity;
import me.dolphago.domain.Followers;
import me.dolphago.domain.Followings;
import me.dolphago.domain.Neighbor;

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

    public static Followers toFollowers(MemberDto memberDto) {
        return new Followers(memberDto.getGithubLogin(), memberDto.getUrl());
    }

    public static Followings toFollowings(MemberDto memberDto) {
        return new Followings(memberDto.getGithubLogin(), memberDto.getUrl());
    }

    public static Neighbor toNeighbor(MemberDto memberDto) {
        return new Neighbor(memberDto.getGithubLogin(), memberDto.getUrl());
    }

//    TODO : 일반화를 시도하려고 했으나 캐스팅이 안됨
//    public static <T extends BaseEntity<T>> T toEntity(MemberDto memberDto, Class<T> cls) {
//        return cls.cast(T.builder()
//                         .url(memberDto.getUrl())
//                         .githubLogin(memberDto.getGithubLogin())
//                         .build());
//    }
}
