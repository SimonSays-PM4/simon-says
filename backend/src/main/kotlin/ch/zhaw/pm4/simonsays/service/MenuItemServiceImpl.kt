package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import org.springframework.stereotype.Service

@Service
class MenuItemServiceImpl(
        private val menuItemRepository: MenuItemRepository,
        private val menuItemMapper: MenuItemMapper,
        private val eventService: EventService,
        private val ingredientService: IngredientService,
        private val ingredientRepository: IngredientRepository,
        private val ingredientMapper: IngredientMapper,
        private val eventMapper: EventMapper
) : MenuItemService {

    override fun getMenuItems(eventId: Long): MutableList<MenuItemDTO> {
        val menuItems: List<MenuItem> = menuItemRepository.findByEventId(eventId)
        val menuItemDTOs: MutableList<MenuItemDTO> = menuItems.map { menuItem ->
            menuItemMapper.mapToMenuItemDTO(menuItem)
        }.toMutableList()
        return menuItemDTOs
    }

    override fun getMenuItem(menuItemId: Long): MenuItemDTO {
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { ResourceNotFoundException("Menu item not found with ID: $menuItemId") }
        return menuItemMapper.mapToMenuItemDTO(menuItem)
    }

    override fun createUpdateMenuItem(menuItem: MenuItemCreateUpdateDTO): MenuItemDTO {
        // Log the start of the operation and the input DTO
        println("Starting createUpdateMenuItem with DTO: $menuItem")

        // Fetching the event based on the eventId
        val event = eventService.getEvent(menuItem.eventId!!)
        println("Fetched event: ${event.id}")

        val ingredients = ingredientRepository.findByIdIn(menuItem.ingredients!!.map { it.id.toInt() })

        // Determine whether this is a creation or update operation
        val isUpdateOperation = menuItem.id != null
        println("Is update operation: $isUpdateOperation")

        val menuItemToBeSaved = if (isUpdateOperation) {
            println("Preparing menuItem for update")
            makeMenuItemReadyForUpdate(menuItem)
        } else {
            println("Mapping DTO to new menuItem entity")
            menuItemMapper.mapCreateDTOToMenuItem(menuItem, event, ingredients)
        }

        // Log the menuItem entity about to be saved
        println("Saving menuItem: ${menuItemToBeSaved.id ?: "new"}")
        val savedMenuItem = menuItemRepository.save(menuItemToBeSaved)

        // Log the result of the save operation
        println("Saved menuItem with ID: ${savedMenuItem.id}")

        // Map the saved entity to a DTO to return
        return menuItemMapper.mapToMenuItemDTO(savedMenuItem).also {
            println("Returning menuItemDTO with ID: ${it.id}")
        }
    }


    private fun makeMenuItemReadyForUpdate(menuItem: MenuItemCreateUpdateDTO): MenuItem {
        val menuItemToSave = menuItemRepository.findById(menuItem.id!!).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: ${menuItem.id}")
        }
        menuItemToSave.name = menuItem.name!!
        menuItemToSave.event = eventService.getEventEntity(menuItem.eventId!!)
        menuItemToSave.ingredients = menuItem.ingredients!!.map {
            ingredientDTO -> ingredientMapper.mapDTOtoIngredient(ingredientService.getIngredient(ingredientDTO.id, menuItemToSave.event.id!!), eventMapper.mapToEventDTO(menuItemToSave.event)) }
        return menuItemToSave
    }

    override fun deleteMenuItem(menuItemId: Long) {
        val menuItem = menuItemRepository.findById(menuItemId).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: $menuItemId")
        }
        menuItemRepository.delete(menuItem)
    }

}