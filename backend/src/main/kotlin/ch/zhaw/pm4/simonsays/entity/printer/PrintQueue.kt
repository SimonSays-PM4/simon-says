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

    @ManyToMany
    val printers: List<Printer>,

    @OneToMany(mappedBy = "printQueue")
    val jobs: List<PrintQueueJob>,

    @ManyToOne
    val printerServer: PrinterServer
)
