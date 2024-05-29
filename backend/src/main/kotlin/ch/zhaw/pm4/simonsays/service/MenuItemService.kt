package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceInUseException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import org.springframework.stereotype.Service

@Service
class MenuItemService(
        private val menuItemRepository: MenuItemRepository,
        private val menuItemMapper: MenuItemMapper,
        private val eventService: EventService,
        private val ingredientRepository: IngredientRepository,
) {

    fun listMenuItems(eventId: Long): MutableList<MenuItemDTO> {
        val menuItems: List<MenuItem> = menuItemRepository.findAllByEventId(eventId)
        val menuItemDTOs: MutableList<MenuItemDTO> = menuItems.map { menuItem ->
            menuItemMapper.mapToMenuItemDTO(menuItem)
        }.toMutableList()
        return menuItemDTOs
    }

    fun getMenuItem(menuItemId: Long, eventId: Long): MenuItemDTO {
        val menuItem = menuItemRepository.findByIdAndEventId(menuItemId, eventId)
            .orElseThrow { ResourceNotFoundException("Menu item not found with ID: $menuItemId") }
        return menuItemMapper.mapToMenuItemDTO(menuItem)
    }

    fun createUpdateMenuItem(menuItem: MenuItemCreateUpdateDTO, eventId: Long): MenuItemDTO {
        val event = eventService.getEvent(eventId)
        val ingredients = ingredientRepository.findByIdIn(menuItem.ingredients!!.map { it.id })
        val isUpdateOperation = menuItem.id != null
        val menuItemToBeSaved = if (isUpdateOperation) {
            makeMenuItemReadyForUpdate(menuItem, eventId, ingredients)
        } else {
            menuItemMapper.mapCreateDTOToMenuItem(menuItem, event, ingredients)
        }
        val savedMenuItem = menuItemRepository.save(menuItemToBeSaved)
        return menuItemMapper.mapToMenuItemDTO(savedMenuItem)
    }


    private fun makeMenuItemReadyForUpdate(menuItem: MenuItemCreateUpdateDTO, eventId: Long, ingredients: List<Ingredient>): MenuItem {
        val menuItemToSave = menuItemRepository.findById(menuItem.id!!).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: ${menuItem.id}")
        }
        menuItemToSave.name = menuItem.name!!
        menuItemToSave.event = eventService.getEventEntity(eventId)
        menuItemToSave.ingredients = ingredients
        menuItemToSave.price = menuItem.price
        return menuItemToSave
    }

    fun deleteMenuItem(menuItemId: Long, eventId: Long) {
        val menuItem = menuItemRepository.findByIdAndEventId(menuItemId, eventId).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: $menuItemId")
        }
        if (menuItem.menus != null && menuItem.menus.isNotEmpty()) {
            throw ResourceInUseException("Menu item is used in menus and cannot be deleted")
        }
        menuItemRepository.delete(menuItem)
    }

}