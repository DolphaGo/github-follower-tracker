package me.dolphago.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
