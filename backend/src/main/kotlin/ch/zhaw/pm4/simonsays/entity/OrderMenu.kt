package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class OrderMenu (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    var menu: Menu,

    @OneToMany(mappedBy = "orderMenu", cascade = [CascadeType.PERSIST])
    var orderMenuItems: MutableSet<OrderMenuItem>,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: FoodOrder? = null,

    @Column(nullable = false)
    var state: State,

    @Column
    var price: Double,

    ){

    fun addOrderMenuItem(orderMenuItem: OrderMenuItem) {
        orderMenuItems.add(orderMenuItem)
        orderMenuItem.orderMenu = this
    }
    override fun hashCode(): Int = id?.hashCode() ?: 0
}