package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long>
