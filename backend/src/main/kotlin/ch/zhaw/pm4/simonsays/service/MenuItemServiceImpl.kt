package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
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
        private val ingredientRepository: IngredientRepository,
) : MenuItemService {

    override fun listMenuItems(eventId: Long): MutableList<MenuItemDTO> {
        val menuItems: List<MenuItem> = menuItemRepository.findAllByEventId(eventId)
        val menuItemDTOs: MutableList<MenuItemDTO> = menuItems.map { menuItem ->
            menuItemMapper.mapToMenuItemDTO(menuItem)
        }.toMutableList()
        return menuItemDTOs
    }

    override fun getMenuItem(menuItemId: Long, eventId: Long): MenuItemDTO {
        val menuItem = menuItemRepository.findByIdAndEventId(menuItemId, eventId)
            .orElseThrow { ResourceNotFoundException("Menu item not found with ID: $menuItemId") }
        return menuItemMapper.mapToMenuItemDTO(menuItem)
    }

    override fun createUpdateMenuItem(menuItem: MenuItemCreateUpdateDTO, eventId: Long): MenuItemDTO {
        val event = eventService.getEvent(eventId)
        val ingredients = ingredientRepository.findByIdIn(menuItem.ingredients!!.map { it.id.toInt() })
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
        return menuItemToSave
    }

    override fun deleteMenuItem(menuItemId: Long, eventId: Long) {
        val menuItem = menuItemRepository.findByIdAndEventId(menuItemId, eventId).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: $menuItemId")
        }
        menuItemRepository.delete(menuItem)
    }

}