package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.OrderIngredient
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import io.socket.socketio.server.SocketIoSocket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StationViewNamespace(
        private val stationRepository: StationRepository,
        private val orderMapper: OrderMapper,
        private val ingredientRepository: IngredientRepository,
        private val orderIngredientRepository: OrderIngredientRepository
): SocketIoNamespace<OrderIngredientDTO> {
    companion object {
        /**
         * Regex pattern to match the namespace. The first group is the printer server id.
         */
        internal val namespacePattern = "^/socket-api/v1/event/(\\d+)/station/view/(\\d+)\$".toRegex()
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
        val stationId = getStationIdFromNamespace(requestedNamespace)

        return doesStationExist(eventId, stationId)
    }

    override fun onConnection(socket: SocketIoSocket) {
        try {
            val namespace = socket.namespace.name
            val eventId = getEventIdFromNamespace(namespace)
            val stationId = getStationIdFromNamespace(namespace)

            subscribeToAssemblyStationEvents.add(socket)
            val initialData = getStationView(eventId, stationId)
            socket.sendPojo(SocketIoNamespace.INITIAL_DATA_EVENT, initialData)
            socket.on(SocketIoNamespace.APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
        } catch (e: Exception) {
            onApplicationError(socket, "INITIAL_DATA_ERROR", "Failed to fetch initial dataset")
        }
    }

    override fun onDisconnect(socket: SocketIoSocket) {
        subscribeToAssemblyStationEvents.remove(socket)
    }

    override fun onRemove(data: OrderIngredientDTO) {
        val subscribers = subscribeToAssemblyStationEvents
        subscribers.forEach { it.sendPojo(SocketIoNamespace.REMOVE_EVENT, data) }
    }

    override fun onChange(data: OrderIngredientDTO) {
        val subscribers = subscribeToAssemblyStationEvents
        subscribers.forEach { it.sendPojo(SocketIoNamespace.CHANGE_EVENT, data) }
    }

    override fun onApplicationError(id: String?, error: ApplicationErrorDto) {
        subscribeToAssemblyStationEvents.forEach { it.sendPojo(SocketIoNamespace.APPLICATION_ERROR_EVENT, error) }
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

    fun doesStationExist(eventId: Long, stationId: Long): Boolean {
        stationRepository.findByIdAndEventId(stationId, eventId)
                .orElseThrow { ResourceNotFoundException("Station not found with ID: $stationId") }
        return true
    }

    fun getStationView(stationId: Long, eventId: Long): List<OrderIngredientDTO> {
        val stationIngredients: List<Ingredient> = ingredientRepository.findAllByStationsIdAndEventId(stationId, eventId)
        val stationIngredientIds: List<Long> = stationIngredients.map { it.id!! }
        val orderIngredients: List<OrderIngredient> = getOrderIngredientByIngredientIds(stationIngredientIds)
        val orderIngredientsDTOs: List<OrderIngredientDTO> = orderIngredients.map { orderMapper.mapOrderIngredientToOrderIngredientDTO(it) }
        return orderIngredientsDTOs
    }

    fun getOrderIngredientByIngredientIds(ingredientIds: List<Long>): List<OrderIngredient> {
        return orderIngredientRepository.findAllByIngredientIdInAndStateEquals(ingredientIds, State.IN_PROGRESS)
    }


}