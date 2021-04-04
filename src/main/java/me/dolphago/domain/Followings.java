package me.dolphago.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Followings extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    String githubLogin;
    String url;

    @Builder
    public Followings(String githubLogin, String url) {
        this.githubLogin = githubLogin;
        this.url = url;
    }
}
