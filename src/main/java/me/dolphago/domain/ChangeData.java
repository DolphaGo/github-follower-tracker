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
public class ChangeData extends BaseEntity<ChangeData> {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private Relation status;

    public ChangeData(String githubLogin, String url, Relation status) {
        super(githubLogin, url);
        this.status = status;
    }
}
