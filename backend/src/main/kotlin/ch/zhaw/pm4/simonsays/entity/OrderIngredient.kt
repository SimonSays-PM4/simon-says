package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class OrderIngredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne
    @JoinColumn(name = "event_id")
    val event: Event,

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient,

    @ManyToOne
    @JoinColumn(name = "orderMenuItem_id")
    val orderMenuItem: OrderMenuItem? = null,

    @Column(nullable = false)
    var state: State,

    )