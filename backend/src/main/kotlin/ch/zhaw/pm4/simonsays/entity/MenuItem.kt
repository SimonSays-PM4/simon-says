package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class MenuItem (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String,

        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(
                name = "menu_item_ingredients", // Specifies the join table
                joinColumns = [JoinColumn(name = "menu_item_id")],
                inverseJoinColumns = [JoinColumn(name = "ingredient_id")]
        )
        var ingredients: List<Ingredient> = mutableListOf(),

        @ManyToOne(optional = false)
        @JoinColumn(name = "event_id", nullable = false)
        var event: Event
)
