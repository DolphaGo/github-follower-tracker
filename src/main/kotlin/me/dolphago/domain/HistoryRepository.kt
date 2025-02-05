package me.dolphago.domain

import org.springframework.data.jpa.repository.JpaRepository

interface HistoryRepository : JpaRepository<History, Long>
