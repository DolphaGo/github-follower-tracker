package me.dolphago.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Neighbor extends BaseEntity<Neighbor> {
    public Neighbor(String githubLogin, String url) {
        super(githubLogin, url);
    }
}
