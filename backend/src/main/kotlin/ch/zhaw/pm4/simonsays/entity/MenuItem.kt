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

        @ManyToOne(optional = false)
        @JoinColumn(name = "event_id", nullable = false)
        var event: Event,

        @ManyToMany(fetch = FetchType.EAGER)
        var ingredients: List<Ingredient>,

        @Column
        var price: Double,

        @ManyToMany(mappedBy = "menuItems")
        val menus: List<Menu>?,

        @OneToMany(mappedBy = "menuItem")
        val orderMenuItem: Set<OrderMenuItem>? = HashSet()
){
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MenuItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (price != other.price) return false

        return true
    }
}
