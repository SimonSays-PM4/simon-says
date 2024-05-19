package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.entity.Station
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import ch.zhaw.pm4.simonsays.service.IngredientService
import ch.zhaw.pm4.simonsays.service.OrderIngredientService
import ch.zhaw.pm4.simonsays.service.StationService
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import io.socket.socketio.server.SocketIoSocket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StationViewNamespace(
        private val stationRepository: StationRepository,
        private val eventRepository: EventRepository,
        private val stationService: StationService,
        private val ingredientService: IngredientService
): SocketIoNamespace<Pair<Long, Long>, OrderIngredientDTO> {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id.
         */
        internal val namespacePattern = "^/socket-api/v1/event/(\\d+)/station/view/(\\d+)\$".toRegex()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * A map of all subscribers that are interested in a specific station. The key is the station id.
     */
    internal val subscribeToSpecificStation = mutableMapOf<Pair<Long, Long>, MutableSet<SocketIoSocket>>()

    override fun isPartOfNamespace(requestedNamespace: String): Boolean {
        // check if matches the namespace pattern
        val matchResult = namespacePattern.matches(requestedNamespace)
        if (!matchResult) return false

        val eventId = getEventIdFromNamespace(requestedNamespace)
        val stationId = getStationIdFromNamespace(requestedNamespace)

        doesEventExist(eventId)
        return doesStationExist(eventId, stationId)
    }

    override fun onConnection(socket: SocketIoSocket) {
        try {
            val namespace = socket.namespace.name
            val eventId = getEventIdFromNamespace(namespace)
            val stationId = getStationIdFromNamespace(namespace)

            val key = Pair(eventId, stationId)
            if (!subscribeToSpecificStation.containsKey(key)) {
                subscribeToSpecificStation[key] = mutableSetOf()
            }
            subscribeToSpecificStation[key]?.add(socket)
            val initialData = stationService.getStationView(stationId, eventId)
            socket.sendPojo(SocketIoNamespace.INITIAL_DATA_EVENT, initialData)
            socket.on(SocketIoNamespace.APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
        } catch (e: Exception) {
            onApplicationError(socket, "INITIAL_DATA_ERROR", "Failed to fetch initial dataset")
        }
    }

    override fun onDisconnect(socket: SocketIoSocket) {
        val socketSubscription = determineSocketSubscription(socket)
        socketSubscription?.remove(socket)
    }

    override fun onRemove(data: OrderIngredientDTO) {
        val stations: List<Station> = stationService.getStationAssociatedWithIngredient(data.id)
        stations.forEach { station ->
            val key = Pair(station.event.id!!, station.id!!)
            if (!subscribeToSpecificStation.containsKey(key)) {
                subscribeToSpecificStation[key] = mutableSetOf()
            }
            val subscribers: MutableSet<SocketIoSocket>? = subscribeToSpecificStation[key]
            subscribers!!.forEach { it.sendPojo(SocketIoNamespace.REMOVE_EVENT, data) }
        }
    }

    override fun onChange(data: OrderIngredientDTO) {
        val ingredient = ingredientService.getIngredientByOrderIngredientId(data.id)
        val stations: List<Station> = stationService.getStationAssociatedWithIngredient(ingredient.id!!)
        stations.forEach { station ->
            val key = Pair(station.event.id!!, station.id!!)
            if (!subscribeToSpecificStation.containsKey(key)) {
                subscribeToSpecificStation[key] = mutableSetOf()
            }
            val subscribers: MutableSet<SocketIoSocket>? = subscribeToSpecificStation[key]
            subscribers!!.forEach { it.sendPojo(SocketIoNamespace.CHANGE_EVENT, data) }
        }
    }

    override fun onApplicationError(id: Pair<Long, Long>?, error: ApplicationErrorDto) {
        if(id != null) {
            subscribeToSpecificStation[id]?.forEach { it.sendPojo(SocketIoNamespace.APPLICATION_ERROR_EVENT, error) }
        }
    }

    fun doesStationExist(eventId: Long, stationId: Long): Boolean {
        stationRepository.findByIdAndEventId(stationId, eventId)
                .orElseThrow { ResourceNotFoundException("Station not found with ID: $stationId") }
        return true
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

    private fun getStationIdFromNamespace(namespace: String): Long {
        val matchResult =
                namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return (matchResult.groups[2]?.value)!!.toLong()
    }

    private fun determineSocketSubscription(socket: SocketIoSocket): MutableSet<SocketIoSocket>? {
        val namespace = socket.namespace.name
        val eventId = getEventIdFromNamespace(namespace)
        val stationId = getStationIdFromNamespace(namespace)
        return subscribeToSpecificStation[Pair(eventId, stationId)]
    }

}