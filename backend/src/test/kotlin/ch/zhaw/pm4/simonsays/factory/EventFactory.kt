package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventFactory(
        @Autowired private val eventRepository: EventRepository
) {
    fun createEvent(name: String = "Default Event Name", password: String = "defaultpassword", numberOfTables: Long = 0): Event {
        val event = Event(name = name, password = password, numberOfTables = numberOfTables)
        return eventRepository.save(event)
    }

}
