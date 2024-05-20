package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import ch.zhaw.pm4.simonsays.service.StationService
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import io.socket.socketio.server.SocketIoSocket
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AssemblyViewNamespace(
    private val eventRepository: EventRepository,
    private val stationRepository: StationRepository,
    private val stationService: StationService
): SocketIoNamespace<String, OrderDTO> {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id.
         */
        internal val namespacePattern = "^/socket-api/v1/event/(\\d+)/station/assembly".toRegex()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * A set of all subscribers that are interested in all printer servers.
     */
    internal val subscribeToAssemblyStationEvents = mutableSetOf<SocketIoSocket>()

    override fun isPartOfNamespace(requestedNamespace: String): Boolean {
        // check if matches the namespace pattern
        val matchResult = namespacePattern.matches(requestedNamespace)
        if (!matchResult) return false

        val eventId = getEventIdFromNamespace(requestedNamespace)

        doesEventExist(eventId)
        return stationService.doesEventHaveAssemblyStation(eventId)
    }

    @Transactional
    override fun onConnection(socket: SocketIoSocket) {
        try {
            val namespace = socket.namespace.name
            val eventId = getEventIdFromNamespace(namespace)

            subscribeToAssemblyStationEvents.add(socket)

            val initialData = stationService.getAssemblyStationView(eventId)
            socket.sendPojo(SocketIoNamespace.INITIAL_DATA_EVENT, initialData)
            socket.on(SocketIoNamespace.APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
        } catch (e: Exception) {
            onApplicationError(socket, "INITIAL_DATA_ERROR", "Failed to fetch initial dataset")
        }
    }

    override fun onDisconnect(socket: SocketIoSocket) {
        subscribeToAssemblyStationEvents.remove(socket)
    }

    override fun onRemove(data: OrderDTO) {
        val subscribers = subscribeToAssemblyStationEvents
        subscribers.forEach { it.sendPojo(SocketIoNamespace.REMOVE_EVENT, data) }
    }

    override fun onChange(data: OrderDTO) {
        val subscribers = subscribeToAssemblyStationEvents
        subscribers.forEach { it.sendPojo(SocketIoNamespace.CHANGE_EVENT, data) }
    }

    override fun onApplicationError(id: String?, error: ApplicationErrorDto) {
        subscribeToAssemblyStationEvents.forEach { it.sendPojo(SocketIoNamespace.APPLICATION_ERROR_EVENT, error) }
    }

    fun doesEventExist(eventId: Long): Boolean {
        eventRepository.findById(eventId)
                .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return true
    }

    private fun getEventIdFromNamespace(namespace: String): Long {
        val matchResult =
                namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return (matchResult.groups[1]?.value)!!.toLong()
    }
}