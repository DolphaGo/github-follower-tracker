package me.dolphago.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class History {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubLogin;
    private String url;

    @Enumerated(EnumType.STRING)
    private Relation relation;

    @CreatedDate
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    public History(final String githubLogin, final String url, final Relation relation) {
        this.githubLogin = githubLogin;
        this.url = url;
        this.relation = relation;
    }
}
