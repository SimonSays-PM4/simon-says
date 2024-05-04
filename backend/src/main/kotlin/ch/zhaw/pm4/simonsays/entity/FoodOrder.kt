package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class FoodOrder (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var menus: MutableList<OrderMenu>? = mutableListOf(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var menuItems: MutableList<OrderMenuItem>? = mutableListOf(),

    @Column(nullable = false)
    var state: State,

    @Column(nullable = true)
    var tableNumber: Long? = null,

    @Column(nullable = false)
    var totalPrice: Double,

    @Column(nullable = false)
    var isTakeAway: Boolean = false
) {
    fun addMenu(menu: OrderMenu) {
        menus?.add(menu)
        menu.order = this
    }

    fun addMenuItem(menuItem: OrderMenuItem) {
        menuItems?.add(menuItem)
        menuItem.order = this
    }

    fun getTakeAwayNr(): Long? {
        return if (isTakeAway) this.id?.rem(1000) else null
    }
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoodOrder) return false

        if (id != other.id) return false
        if (state != other.state) return false
        if (tableNumber != other.tableNumber) return false
        if (totalPrice != other.totalPrice) return false
        if (isTakeAway != other.isTakeAway) return false
        if (event.id != other.event.id) return false

        return true
    }
}
