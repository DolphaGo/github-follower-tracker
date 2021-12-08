package me.dolphago.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Entity
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubLogin;
    private String url;

    @Enumerated(EnumType.STRING)
    private Relation relation;

    @Builder
    public History(final String githubLogin, final String url, final Relation relation) {
        this.githubLogin = githubLogin;
        this.url = url;
        this.relation = relation;
    }
}
