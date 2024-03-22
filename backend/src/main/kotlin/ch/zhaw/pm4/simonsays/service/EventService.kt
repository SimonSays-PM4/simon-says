package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.EventDTO

interface EventService {
    fun createEvent(): EventDTO
}