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
)