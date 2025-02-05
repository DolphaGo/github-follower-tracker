package me.dolphago.domain

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
data class History(
    val githubLogin: String,
    val url: String,
    @field:Enumerated(EnumType.STRING)
    val relation: Relation
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
}
