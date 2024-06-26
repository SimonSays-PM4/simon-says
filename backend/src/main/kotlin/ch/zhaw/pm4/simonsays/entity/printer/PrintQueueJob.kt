package ch.zhaw.pm4.simonsays.entity.printer

import ch.zhaw.pm4.simonsays.entity.NoArgAnnotation
import jakarta.persistence.*

@Entity
@NoArgAnnotation
data class PrintQueueJob(
    @Id
    val id: String,

    @Column(nullable = false)
    val status: JobStatus,

    @Column(nullable = true)
    val statusMessage: String? = null,

    @Column(nullable = true, length = 8164)
    val base64PngLogoImage: String? = null,

    @Column(nullable = true)
    val header: String? = null,

    @Column(nullable = true)
    val title: String? = null,

    @Column(nullable = false, length = 1023)
    val body: String,

    @Column(nullable = true, length = 1023)
    val qrCode: String? = null,

    @Column(nullable = true)
    val footer: String? = null,

    @Column(nullable = false)
    val creationDateTime: Long,

    @Column(nullable = false)
    val lastUpdateDateTime: Long,

    @ManyToOne
    val printQueue: PrintQueue
)
