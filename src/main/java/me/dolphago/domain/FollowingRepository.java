package me.dolphago.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    // when I tested, githubId is not unique(not unique values often appeared), it is unique 'github_loginId'
    Following findByGithubLogin(String githubLogin);
}
