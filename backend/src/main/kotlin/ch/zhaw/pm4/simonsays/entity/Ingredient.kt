package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @ManyToOne
    @JoinColumn(name = "event_id")
    val event: Event,

    @ManyToMany(mappedBy = "ingredients")
    val menuItems: List<MenuItem>?

)
