package me.dolphago.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.dolphago.domain.BaseEntity;

@ToString
@Getter
@NoArgsConstructor
public class MemberDto {
    private String login;
    private String url;

    public MemberDto(String login, String url) {
        this.login = login;
        this.url = url;
    }

    public static MemberDto from(BaseEntity entity) {
        return new MemberDto(entity.getGithubLogin(), entity.getUrl());
    }
}
