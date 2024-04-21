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

    @OneToMany(mappedBy = "order")
    val menus: Set<OrderMenu>? = HashSet(),

    @OneToMany(mappedBy = "order")
    var menuItems: Set<OrderMenuItem>? = HashSet(),

    @Column(nullable = false)
    var state: State,

    @Column(nullable = true)
    var tableNumber: Long? = null,

    @Column(nullable = false)
    var totalPrice: Double
)
