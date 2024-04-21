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

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(optional = false)
    @JoinColumn(name = "menuItem_id", nullable = false)
    var menuItem: MenuItem,

    @OneToMany(mappedBy = "orderMenuItem", cascade = [CascadeType.PERSIST])
    var orderIngredients: Set<OrderIngredient>,

    @Column
    var price: Double,

    @ManyToOne
    @JoinColumn(name = "orderMenu_id")
    var orderMenu: OrderMenu?,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: FoodOrder? = null,

    @Column(nullable = false)
    var state: State,
)
