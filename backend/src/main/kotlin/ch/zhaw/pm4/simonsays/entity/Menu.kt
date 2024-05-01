package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
class Menu (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToMany(fetch = FetchType.EAGER)
    var menuItems: List<MenuItem>,

    @OneToMany(mappedBy = "menu")
    val orderMenu: Set<OrderMenu>? = HashSet()

){
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Menu) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (event.id != other.event.id) return false

        return true
    }
}