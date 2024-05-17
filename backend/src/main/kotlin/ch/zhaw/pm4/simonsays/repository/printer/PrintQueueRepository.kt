package ch.zhaw.pm4.simonsays.repository.printer

import ch.zhaw.pm4.simonsays.entity.printer.PrintQueue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PrintQueueRepository : JpaRepository<PrintQueue, String>