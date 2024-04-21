package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class OrderMenuItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menuItem_id", nullable = false)
    var menuItem: MenuItem,

    @OneToMany(mappedBy = "orderMenuItem", cascade = [CascadeType.PERSIST])
    var orderIngredients: MutableSet<OrderIngredient>,

    @Column
    var price: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderMenu_id")
    var orderMenu: OrderMenu?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: FoodOrder? = null,

    @Column(nullable = false)
    var state: State,
) {
    fun addOrderIngredient(orderIngredient: OrderIngredient) {
        orderIngredients.add(orderIngredient)
        orderIngredient.orderMenuItem = this
    }
    override fun hashCode(): Int = id?.hashCode() ?: 0
}
