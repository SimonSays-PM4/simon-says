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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderMenuItem_id")
    var orderMenuItem: OrderMenuItem? = null,

    @Column(nullable = false)
    var state: State,

    ){
    override fun hashCode(): Int = id?.hashCode() ?: 0
}