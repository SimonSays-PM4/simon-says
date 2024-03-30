package ch.zhaw.pm4.simonsays.entity.printer

import ch.zhaw.pm4.simonsays.entity.NoArgAnnotation
import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class Printer(
    @Id
    val mac: String,

    @Column(nullable = false)
    val name: String,

    @ManyToMany
    val printQueues: List<PrintQueue>
)