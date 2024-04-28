package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Station (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var assemblyStation: Boolean,

    @ManyToOne
    @JoinColumn(name = "event_id")
    var event: Event,

    @ManyToMany(fetch = FetchType.EAGER)
    var ingredients: List<Ingredient>
){
    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Station) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (assemblyStation != other.assemblyStation) return false
        if (event.id != other.event.id) return false

        return true
    }
}