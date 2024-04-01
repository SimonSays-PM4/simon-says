package ch.zhaw.pm4.simonsays.entity

import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Ingredient(

    @Column(nullable = false)
    val name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)
