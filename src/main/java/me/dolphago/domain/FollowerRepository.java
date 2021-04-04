package me.dolphago.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Followers, Long> {
    Optional<Followers> findByGithubLogin(String githubLogin);
}
