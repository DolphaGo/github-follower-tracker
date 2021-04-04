package me.dolphago.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = "id")
@NoArgsConstructor
@Getter
@Entity
public class ChangeData extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String githubLogin;
    private String url;

    @Enumerated(EnumType.STRING)
    private Relation status;

    @Builder
    public ChangeData(String githubLogin, String url, Relation status) {
        this.githubLogin = githubLogin;
        this.url = url;
        this.status = status;
    }
}
