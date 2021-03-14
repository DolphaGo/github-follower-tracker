package me.dolphago.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Followings, Long> {
    // when I tested, githubId is not unique(not unique values often appeared), it is unique 'github_loginId'
    // TODO : Next time you use DB, use by loginId
    Optional<Followings> findByGithubId(Long githubId);
}
