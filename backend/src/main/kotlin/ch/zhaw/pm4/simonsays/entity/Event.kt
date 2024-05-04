package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Event(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var numberOfTables: Long,

    @OneToMany(mappedBy = "event")
    val ingredients: Set<Ingredient>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val menuItems: Set<MenuItem>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val menus: Set<Menu>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val stations: Set<Station>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val order: Set<FoodOrder>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val orderIngredient: Set<OrderIngredient>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val orderMenuItem: Set<OrderMenuItem>? = HashSet(),

    @OneToMany(mappedBy = "event")
    val orderMenu: Set<OrderMenu>? = HashSet()
){
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (password != other.password) return false
        if (numberOfTables != other.numberOfTables) return false

        return true
    }
}