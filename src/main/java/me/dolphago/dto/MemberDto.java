package me.dolphago.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.dolphago.domain.BaseClass;
import me.dolphago.domain.Followings;

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

    public static MemberDto from(BaseClass entity) {
        return new MemberDto(entity.getGithubLogin(), entity.getUrl());
    }

    public static <T> T toEntity(MemberDto memberDto, Class<T> cls) {
        return cls.cast(T.
                           .githubLogin(memberDto.getGithubLogin())
                           .url(memberDto.getUrl())
                           .build());
    }

//    public static <T extends BaseClass> T toEntity(MemberDto memberDto, Class<T> cls) {
//        return cls.cast(T.builder()
//                         .githubLogin(memberDto.getGithubLogin())
//                         .url(memberDto.getUrl())
//                         .build());
//    }
}
