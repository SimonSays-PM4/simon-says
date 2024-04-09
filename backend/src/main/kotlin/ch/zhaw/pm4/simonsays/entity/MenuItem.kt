package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

@Entity
@NoArgAnnotation
data class MenuItem (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String,

        @ManyToMany(fetch = FetchType.EAGER)
        val ingredients: List<Ingredient>,

        @ManyToOne(optional = false)
        @JoinColumn(name = "event_id", nullable = false)
        var event: Event
)
