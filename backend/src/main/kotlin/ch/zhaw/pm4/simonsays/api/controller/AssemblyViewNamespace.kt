package ch.zhaw.pm4.simonsays.api.controller

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.api.types.printer.ApplicationErrorDto
import ch.zhaw.pm4.simonsays.entity.FoodOrder
import ch.zhaw.pm4.simonsays.entity.OrderMenu
import ch.zhaw.pm4.simonsays.entity.OrderMenuItem
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.*
import ch.zhaw.pm4.simonsays.utils.printer.sendPojo
import io.socket.socketio.server.SocketIoSocket
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AssemblyViewNamespace(
    private val eventRepository: EventRepository,
    private val stationRepository: StationRepository,
    private val orderRepository: OrderRepository,
    private val orderMenuRepository: OrderMenuRepository,
    private val orderMenuItemRepository: OrderMenuItemRepository,
    private val orderMapper: OrderMapper,
): SocketIoNamespace<OrderDTO> {
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
        return doesEventHaveAssemblyStation(eventId)

    }

    @Transactional
    override fun onConnection(socket: SocketIoSocket) {
        try {
            val namespace = socket.namespace.name
            val eventId = getEventIdFromNamespace(namespace)

            subscribeToAssemblyStationEvents.add(socket)

            val initialData = getAssemblyStationView(eventId)
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

    private fun getEventIdFromNamespace(namespace: String): Long {
        val matchResult =
                namespacePattern.matchEntire(namespace) ?: throw IllegalArgumentException("Invalid namespace: $namespace")
        return (matchResult.groups[1]?.value)!!.toLong()
    }

    fun doesEventHaveAssemblyStation(eventId: Long): Boolean {
        stationRepository.findByEventIdAndAssemblyStation(eventId, true).orElseThrow {
            ResourceNotFoundException("The event with id: $eventId does not yet have an assembly station")
        }
        return true
    }

    fun doesEventExist(eventId: Long): Boolean {
        eventRepository.findById(eventId)
                .orElseThrow { ResourceNotFoundException("Event not found with ID: $eventId") }
        return true
    }

    fun getAssemblyStationView(eventId: Long): List<OrderDTO> {
        doesEventHaveAssemblyStation(eventId)
        val orders: List<FoodOrder> = orderRepository.findAllByEventIdAndStateEquals(eventId, State.IN_PROGRESS)
        val processedOrders: MutableList<FoodOrder> = mutableListOf()
        orders.forEach { foodOrder ->
            foodOrder.menus = getOrderMenus(eventId, foodOrder.id!!)
            foodOrder.menuItems = getOrderMenuItems(eventId, foodOrder.id)

            if(foodOrder.menus!!.isNotEmpty() || foodOrder.menuItems!!.isNotEmpty()) {
                processedOrders.add(foodOrder)
            }

        }
        return processedOrders.map { orderMapper.mapOrderToOrderDTO(it) }
    }

    fun getOrderMenuItems(eventId: Long, orderId: Long): MutableList<OrderMenuItem> {
        val menuItems: MutableList<OrderMenuItem> = orderMenuItemRepository.findAllByStateEqualsAndOrderIdEqualsAndOrderMenuEquals(State.IN_PROGRESS, orderId, null)
        val processedMenuItems: MutableList<OrderMenuItem> = mutableListOf()
        menuItems.forEach { orderMenuItem ->
            var allIngredientsComplete = true
            orderMenuItem.orderIngredients.forEach { orderIngredient ->
                if(orderIngredient.state != State.DONE) {
                    allIngredientsComplete = false
                }
            }
            if(allIngredientsComplete) {
                processedMenuItems.add(orderMenuItem)
            }
        }
        return processedMenuItems
    }

    fun getOrderMenus(eventId: Long, orderId: Long): MutableList<OrderMenu> {
        val menus = orderMenuRepository.findAllByStateEqualsAndOrderIdEquals(State.IN_PROGRESS, orderId)
        val processedMenus: MutableList<OrderMenu> = mutableListOf()
        menus.forEach { orderMenu ->
            var menuReady = true
            orderMenu.orderMenuItems.forEach {orderMenuItem ->
                orderMenuItem.orderIngredients.forEach { orderIngredient ->
                    if(orderIngredient.state != State.DONE) {
                        menuReady = false
                    }
                }
            }

            if(menuReady) {
                processedMenus.add(orderMenu)
            }

        }

        return processedMenus
    }


}