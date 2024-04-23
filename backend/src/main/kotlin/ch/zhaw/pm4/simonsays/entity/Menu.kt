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

)