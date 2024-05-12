package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var mustBeProduced: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "event_id")
    val event: Event,

    @ManyToMany(mappedBy = "ingredients")
    val menuItems: List<MenuItem>?,

    @ManyToMany(mappedBy = "ingredients")
    val stations: List<Station>?,

    @OneToMany(mappedBy = "ingredient")
    val orderIngredients: Set<OrderIngredient>? = HashSet()

) {
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ingredient) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (mustBeProduced != other.mustBeProduced) return false
        if (event.id != other.event.id) return false

        return true
    }
}
