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
        return Followers.builder()
                        .githubLogin(memberDto.getGithubLogin())
                        .url(memberDto.getUrl())
                        .build();
    }

    public static Followings toFollowings(MemberDto memberDto) {
        return Followings.builder()
                         .githubLogin(memberDto.getGithubLogin())
                         .url(memberDto.getUrl())
                         .build();
    }

    public static Neighbor toNeighbor(MemberDto memberDto) {
        return Neighbor.builder()
                       .githubLogin(memberDto.getGithubLogin())
                       .url(memberDto.getUrl())
                       .build();
    }

//    public static <T> T toEntity(MemberDto memberDto, Class<T> cls) { // 이런식으로 구현하고 싶은데 ㅠ.
//        return cls.cast(T.builder()
//                         .githubLogin(memberDto.getGithubLogin())
//                         .url(memberDto.getUrl())
//                         .build());
//    }
}
