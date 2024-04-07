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
        val id: Long? = null,

        @OneToMany(mappedBy = "event")
        val ingredients: Set<Ingredient>? = HashSet(),

        @OneToMany(mappedBy = "event")
        val menuItems: Set<MenuItem>? = HashSet()
)