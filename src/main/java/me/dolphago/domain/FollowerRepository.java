package me.dolphago.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    Follower findByGithubLogin(String githubLogin);
}
