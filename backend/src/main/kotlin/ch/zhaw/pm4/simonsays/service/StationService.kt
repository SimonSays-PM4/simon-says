package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.mapper.StationMapper
import ch.zhaw.pm4.simonsays.api.types.OrderDTO
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.AssemblyStationAlreadyDefinedException
import ch.zhaw.pm4.simonsays.exception.ResourceInUseException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import org.springframework.stereotype.Service

@Service
class StationService(
        private val stationRepository: StationRepository,
        private val stationMapper: StationMapper,
        private val eventService: EventService,
        private val ingredientRepository: IngredientRepository,
        private val orderRepository: OrderRepository,
        private val orderMapper: OrderMapper,
        private val orderIngredientService: OrderIngredientService,
        private val orderMenuItemService: OrderMenuItemService,
        private val orderMenuService: OrderMenuService,
) {

    fun listStations(eventId: Long): MutableList<StationDTO> {
        val stations: List<Station> = stationRepository.findAllByEventId(eventId)
        val stationDTOs: MutableList<StationDTO> = stations.map { station ->
            stationMapper.mapToStationDTO(station)
        }.toMutableList()
        return stationDTOs
    }

    fun getStation(stationId: Long, eventId: Long): StationDTO {
        val station = getStationEntity(stationId, eventId)
        return stationMapper.mapToStationDTO(station)
    }

    fun createUpdateStation(station: StationCreateUpdateDTO, eventId: Long): StationDTO {
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

    fun getStationView(stationId: Long, eventId: Long): List<OrderIngredientDTO> {
            val stationIngredients: List<Ingredient> = ingredientRepository.findAllByStationsIdAndEventId(stationId, eventId)
            val stationIngredientIds: List<Long> = stationIngredients.map { it.id!! }
            val orderIngredients: List<OrderIngredient> = orderIngredientService.getOrderIngredientByIngredientIds(stationIngredientIds)
            val orderIngredientsDTOs: List<OrderIngredientDTO> = orderIngredients.map { orderMapper.mapOrderIngredientToOrderIngredientDTO(it) }
            return orderIngredientsDTOs
    }

    fun getAssemblyStationView(eventId: Long): List<OrderDTO> {
        doesEventHaveAssemblyStation(eventId)
        val orders: List<FoodOrder> = orderRepository.findAllByEventIdAndStateEquals(eventId, State.IN_PROGRESS)
        val processedOrders: MutableList<FoodOrder> = mutableListOf()
        orders.forEach { foodOrder ->
            foodOrder.menus = orderMenuService.getOrderMenus(eventId, foodOrder.id!!)
            foodOrder.menuItems = orderMenuItemService.getOrderMenuItems(eventId, foodOrder.id)

            if(foodOrder.menus!!.isNotEmpty() || foodOrder.menuItems!!.isNotEmpty()) {
                processedOrders.add(foodOrder)
            }

        }
        return processedOrders.map { orderMapper.mapOrderToOrderDTO(it) }
    }

    fun deleteStation(stationId: Long, eventId: Long) {
        val station = getStationEntity(stationId, eventId)
        if(station.ingredients.isNotEmpty()) {
            throw ResourceInUseException("Station is used in ingredients and cannot be deleted")
        }
        stationRepository.delete(station)
    }

    fun doesEventHaveAssemblyStation(eventId: Long): Boolean {
        stationRepository.findByEventIdAndAssemblyStation(eventId, true).orElseThrow {
            ResourceNotFoundException("The event with id: $eventId does not yet have an assembly station")
        }
        return true
    }

    fun getStationAssociatedWithIngredient(ingredientId: Long): List<Station> {
        return stationRepository.findAllByIngredientsId(ingredientId)
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

    private fun getStationEntity(stationId: Long, eventId: Long): Station {
        return stationRepository.findByIdAndEventId(stationId, eventId)
                .orElseThrow { ResourceNotFoundException("Station not found with ID: $stationId") }
    }

}