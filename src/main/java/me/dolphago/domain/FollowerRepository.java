package me.dolphago.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Followers, Long> {
}
