package ch.zhaw.pm4.simonsays.entity.printer

import ch.zhaw.pm4.simonsays.entity.NoArgAnnotation
import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class PrinterServer(
    @Id
    val id: String,

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    val queues: List<PrintQueue>
)
