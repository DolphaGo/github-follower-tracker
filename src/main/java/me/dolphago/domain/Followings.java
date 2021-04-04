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
public class Followings extends BaseClass {
    @Id
    @GeneratedValue
    private Long id;

    @Builder
    public Followings(String githubLogin, String url) {
        super(githubLogin, url);
    }
}
