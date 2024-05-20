package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import ch.zhaw.pm4.simonsays.service.StationService
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import io.socket.socketio.server.SocketIoSocket
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AssemblyViewNamespace(
    private val eventRepository: EventRepository,
    private val stationService: StationService,
    private val orderRepository: OrderRepository
): SocketIoNamespace<Long, OrderDTO> {
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
    internal val subscribeToAssemblyStationEvents = mutableMapOf<Long,MutableSet<SocketIoSocket>>()

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

            // ich glaub es git eleganteri version zum d löse so mit functional programming falls lust hesch
            if (!subscribeToAssemblyStationEvents.containsKey(eventId)) {
                subscribeToAssemblyStationEvents[eventId] = mutableSetOf()
                subscribeToAssemblyStationEvents[eventId]?.add(socket)
            } else {
                subscribeToAssemblyStationEvents[eventId]?.add(socket)
            }
            val initialData = stationService.getAssemblyStationView(eventId)
            socket.sendPojo(SocketIoNamespace.INITIAL_DATA_EVENT, initialData)
            socket.on(SocketIoNamespace.APPLICATION_ERROR_EVENT) { error -> log.warn("Received application error event: $error from socket ${socket.id} with namespace $namespace") }
        } catch (e: Exception) {
            onApplicationError(socket, "INITIAL_DATA_ERROR", "Failed to fetch initial dataset")
        }
    }

    override fun onDisconnect(socket: SocketIoSocket) {
        val eventId: Long = getEventIdFromNamespace(socket.namespace.name)
        if(subscribeToAssemblyStationEvents.containsKey(eventId)) {
            subscribeToAssemblyStationEvents[eventId]?.remove(socket)
        }
    }

    override fun onRemove(data: OrderDTO) {
        val order: FoodOrder = getOrder(data.id)
        val subscribers = subscribeToAssemblyStationEvents[order.event.id] // <- da schicksch eifach ah ali lol
        subscribers?.forEach { it.sendPojo(SocketIoNamespace.REMOVE_EVENT, data) }
    }

    override fun onChange(data: OrderDTO) {
        val order: FoodOrder = getOrder(data.id)
        val subscribers = subscribeToAssemblyStationEvents[order.event.id] // <- da schicksch eifach ah ali lol
        subscribers?.forEach { it.sendPojo(SocketIoNamespace.CHANGE_EVENT, data) }
    }

    override fun onApplicationError(id: Long?, error: ApplicationErrorDto) {
        if(id == null){
            // send to all
            subscribeToAssemblyStationEvents.forEach { it.value.forEach{ onApplicationError(it, error) } }
        } else {
            subscribeToAssemblyStationEvents[id]?.forEach { onApplicationError(it, error)  }
        }
    }

    fun doesEventExist(eventId: Long): Boolean {
        eventRepository.findById(eventId)
                .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return true
    }

    private fun getOrder(orderId: Long): FoodOrder {
        val order = orderRepository.findById(orderId).orElseThrow {
            ResourceNotFoundException("Order not found with ID: $orderId")
        }
        return order
    }

    private fun getEventIdFromNamespace(namespace: String): Long {
        val matchResult =
                namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return (matchResult.groups[1]?.value)!!.toLong()
    }
}