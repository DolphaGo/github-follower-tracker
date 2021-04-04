package me.dolphago.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = "id")
@NoArgsConstructor
@Getter
@Entity
public class ChangeData extends BaseClass {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Relation status;

    @Builder
    public ChangeData(String login, String url, Relation status) {
        BaseClass.of()
        this.status = status;
    }
}
