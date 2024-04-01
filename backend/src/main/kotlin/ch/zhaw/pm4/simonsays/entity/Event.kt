package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*


@Entity
@NoArgAnnotation
data class Event(
        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var password: String,

        @Column(nullable = false)
        var numberOfTables: Long,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null
)