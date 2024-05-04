package ch.zhaw.pm4.simonsays.entity.printer

import ch.zhaw.pm4.simonsays.entity.NoArgAnnotation
import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class PrintQueue(
    @Id
    val id: String,

    @Column(nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val printers: List<Printer>,

    @OneToMany(mappedBy = "printQueue")
    val jobs: List<PrintQueueJob>?,

    @ManyToMany(mappedBy = "queues")
    val printerServer: List<PrinterServer>?,
)
