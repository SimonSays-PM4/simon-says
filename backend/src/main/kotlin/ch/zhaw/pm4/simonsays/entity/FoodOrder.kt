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
    val menus: MutableSet<OrderMenu>? = HashSet(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var menuItems: MutableSet<OrderMenuItem>? = HashSet(),

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
}
