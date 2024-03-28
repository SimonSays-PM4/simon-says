package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*


@Entity
@NoArgAnnotation
data class Event(
        @Column(nullable = false)
        val name: String,

        @Column(nullable = false)
        val password: String,

        @Column(nullable = false)
        val numberOfTables: Long,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null
)