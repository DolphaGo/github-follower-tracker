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
public class Neighbor extends BaseEntity<Neighbor> {
    @Id
    @GeneratedValue
    private Long id;

    public Neighbor(String githubLogin, String url) {
        super(githubLogin, url);
    }
}
