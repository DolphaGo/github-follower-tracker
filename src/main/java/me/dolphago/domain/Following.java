package me.dolphago.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@NoArgsConstructor
@Getter
public class Following extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubLogin;
    private String url;

    public Following(final String githubLogin, final String url) {
        this.githubLogin = githubLogin;
        this.url = url;
    }
}
