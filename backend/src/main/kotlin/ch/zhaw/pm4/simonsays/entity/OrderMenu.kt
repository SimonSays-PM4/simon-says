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

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    var menu: Menu,

    @OneToMany(mappedBy = "orderMenu")
    var orderMenuItems: Set<OrderMenuItem>,

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    val order: FoodOrder? = null,

    @Column(nullable = false)
    var state: State,

    @Column
    var price: Double,

    )