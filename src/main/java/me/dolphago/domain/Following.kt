package me.dolphago.domain

import jakarta.persistence.*

@Entity
data class Following(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(name = "github_login")
    val githubLogin: String,
    @Column(name = "url")
    val url: String
) : BaseEntity()