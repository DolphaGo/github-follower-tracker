package me.dolphago.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NeighborRepository extends JpaRepository<Neighbor, Long> {
    Optional<Neighbor> findByGithubLogin(String login);
}
