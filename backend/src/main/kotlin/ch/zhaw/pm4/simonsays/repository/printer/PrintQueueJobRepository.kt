package ch.zhaw.pm4.simonsays.repository.printer

import ch.zhaw.pm4.simonsays.entity.printer.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PrintQueueJobRepository : JpaRepository<PrintQueueJob, String> {

    fun findFirstByPrintQueueAndStatusOrderByCreationDateTimeAsc(printQueue: PrintQueue, status: JobStatus): Optional<PrintQueueJob>

    fun findAllByPrintQueue(printQueue: PrintQueue): List<PrintQueueJob>
}