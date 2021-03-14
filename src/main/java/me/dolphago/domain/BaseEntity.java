package me.dolphago.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseEntity {
    @Column(unique = true)
    private Long githubId;
    private String githubLogin;

    @CreatedDate
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    protected BaseEntity(Long githubId, String githubLogin) {
        this.githubId = githubId;
        this.githubLogin = githubLogin;
    }
}
