package me.dolphago.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Followings, Long> {
    Optional<Followings> findByGithubId(Long githubId);
}
