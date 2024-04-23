package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*
import java.awt.Menu


@Entity
@NoArgAnnotation
data class Event(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var password: String,

        @Column(nullable = false)
        var numberOfTables: Long,

        @OneToMany(mappedBy = "event")
        val ingredients: Set<Ingredient>? = HashSet(),

        @OneToMany(mappedBy = "event")
        val menuItems: Set<MenuItem>? = HashSet(),

        @OneToMany(mappedBy = "event")
        val menus: Set<Menu>? = HashSet(),

        @OneToMany(mappedBy = "event")
        val stations: Set<Station>? = HashSet()
)