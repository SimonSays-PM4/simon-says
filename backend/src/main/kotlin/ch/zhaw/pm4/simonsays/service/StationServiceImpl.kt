package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.mapper.StationMapper
import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.AssemblyStationAlreadyDefinedException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import org.springframework.stereotype.Service

@Service
class StationServiceImpl(
        private val stationRepository: StationRepository,
        private val stationMapper: StationMapper,
        private val eventService: EventService,
        private val ingredientRepository: IngredientRepository,
        private val orderIngredientRepository: OrderIngredientRepository,
        private val orderRepository: OrderRepository,
        private val orderService: OrderService,
        private val orderMapper: OrderMapper
) : StationService {

    override fun listStations(eventId: Long): MutableList<StationDTO> {
        val stations: List<Station> = stationRepository.findAllByEventId(eventId)
        val stationDTOs: MutableList<StationDTO> = stations.map { station ->
            stationMapper.mapToStationDTO(station)
        }.toMutableList()
        return stationDTOs
    }

    override fun getStation(stationId: Long, eventId: Long): StationDTO {
        val station = stationRepository.findByIdAndEventId(stationId, eventId)
                .orElseThrow { ResourceNotFoundException("Station not found with ID: $stationId") }
        return stationMapper.mapToStationDTO(station)
    }

    override fun createUpdateStation(station: StationCreateUpdateDTO, eventId: Long): StationDTO {
        val event = eventService.getEvent(eventId)
        var ingredients: List<Ingredient> = listOf()
        if(station.assemblyStation!!) {
            val assemblyStationFound = stationRepository.findByEventIdAndAssemblyStation(eventId, true)
            if(assemblyStationFound.isPresent && station.id != assemblyStationFound.get().id) {
                throw AssemblyStationAlreadyDefinedException()
            }
        } else {
            ingredients = ingredientRepository.findByIdIn(station.ingredients!!.map { it.id })
        }

        val isUpdateOperation = station.id != null
        val stationToBeSaved = if (isUpdateOperation) {
            makeStationReadyForUpdate(station, eventId, ingredients)
        } else {
            stationMapper.mapCreateDTOToStation(station, event, ingredients)
        }
        val savedStation = stationRepository.save(stationToBeSaved)
        return stationMapper.mapToStationDTO(savedStation)
    }

    override fun getStationView(stationId: Long, eventId: Long): List<OrderIngredientDTO> {
            val stationIngredients: List<Ingredient> = ingredientRepository.findAllByStationsIdAndEventId(stationId, eventId)
            val stationIngredientIds: List<Long> = stationIngredients.map { it.id!! }
            val orderIngredients: List<OrderIngredient> = orderService.getOrderIngredientByIngredientIds(stationIngredientIds)
            val orderIngredientsDTOs: List<OrderIngredientDTO> = orderIngredients.map { orderMapper.mapOrderIngredientToOrderIngredientDTO(it) }
            return orderIngredientsDTOs
    }

    override fun getAssemblyStationView(eventId: Long): List<OrderDTO> {
        stationRepository.findByEventIdAndAssemblyStation(eventId, true).orElseThrow {
            ResourceNotFoundException("The event with id: $eventId does not yet have an assembly station")
        }
        val orders: List<FoodOrder> = orderRepository.findAllByEventIdAndStateEquals(eventId, State.IN_PROGRESS)
        val processedOrders: MutableList<FoodOrder> = mutableListOf()
        orders.forEach { foodOrder ->
            foodOrder.menus = orderService.getOrderMenus(eventId, foodOrder.id!!)
            foodOrder.menuItems = orderService.getOrderMenuItems(eventId, foodOrder.id)

            if(!foodOrder.menus!!.isEmpty() || !foodOrder.menuItems!!.isEmpty()) {
                processedOrders.add(foodOrder)
            }

        }
        return processedOrders.map { orderMapper.mapOrderToOrderDTO(it) }
    }

    override fun processIngredient(eventId: Long, stationId: Long, orderIngredientUpdate: OrderIngredientUpdateDTO): OrderIngredientDTO {
        val stationIngredients: List<Ingredient> = ingredientRepository.findAllByStationsIdAndEventId(stationId, eventId)
        val stationIngredientIds: List<Long> = stationIngredients.map { it.id!! }
        val orderIngredient: OrderIngredient = orderIngredientRepository.findByIdAndEventId(orderIngredientUpdate.id, eventId).orElseThrow() {
            ResourceNotFoundException("No order ingredient found with the ID: ${orderIngredientUpdate.id}")
        }
        if(!stationIngredientIds.contains(orderIngredient.ingredient.id)) {
            throw ValidationException(
                    "This station is not allowed to update the state of the ingredient with the id: ${orderIngredient.id} (${orderIngredient.ingredient.name})"
            )
        }
        return orderService.updateOrderIngredientState(eventId, orderIngredient.id!!)
    }

    private fun makeStationReadyForUpdate(station: StationCreateUpdateDTO, eventId: Long, ingredients: List<Ingredient>): Station {
        val stationToSave = stationRepository.findById(station.id!!).orElseThrow {
            ResourceNotFoundException("Station not found with ID: ${station.id}")
        }
        stationToSave.name = station.name!!
        stationToSave.assemblyStation = station.assemblyStation!!
        stationToSave.event = eventService.getEventEntity(eventId)
        if(!station.assemblyStation) {
            stationToSave.ingredients = ingredients
        }
        return stationToSave
    }

    override fun deleteStation(stationId: Long, eventId: Long) {
        val menuItem = stationRepository.findByIdAndEventId(stationId, eventId).orElseThrow {
            ResourceNotFoundException("Station not found with ID: $stationId")
        }
        stationRepository.delete(menuItem)
    }

}