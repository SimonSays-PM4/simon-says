package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.MenuMapper
import ch.zhaw.pm4.simonsays.api.types.MenuCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.entity.Menu
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.MenuRepository
import org.springframework.stereotype.Service

@Service
class MenuServiceImpl(
    private val menuRepository: MenuRepository,
    private val menuMapper: MenuMapper,
    private val eventService: EventService,
    private val menuItemRepository: MenuItemRepository,
): MenuService {
    override fun listMenus(eventId: Long): MutableList<MenuDTO> {
        val menus: List<Menu> = menuRepository.findAllByEventId(eventId)
        val menuDTOs: MutableList<MenuDTO> = menus.map { menu ->
            menuMapper.mapToMenuDTO(menu, menu.menuItems.sumOf { it.price.toDouble() })
        }.toMutableList()
        return menuDTOs
    }
    override fun getMenu(menuId: Long, eventId: Long): MenuDTO {
        val menu = menuRepository.findByIdAndEventId(menuId, eventId)
            .orElseThrow { ResourceNotFoundException("Menu not found with ID: $menuId") }
        return menuMapper.mapToMenuDTO(menu, menu.menuItems.sumOf { it.price.toDouble() })
    }

    override fun createUpdateMenu(menu: MenuCreateUpdateDTO, eventId: Long): MenuDTO {
        val event = eventService.getEvent(eventId)
        val menuItems = menuItemRepository.findByIdIn(menu.menuItems!!.map { it.id })
        val isUpdateOperation = menu.id != null
        val menuToBeSaved = if (isUpdateOperation) {
            makeMenuReadyForUpdate(menu, eventId, menuItems)
        } else {
            menuMapper.mapCreateDTOToMenu(menu, event, menuItems)
        }
        val savedMenu = menuRepository.save(menuToBeSaved)
        return menuMapper.mapToMenuDTO(savedMenu, menu.menuItems.sumOf { it.price.toDouble() })
    }

    override fun deleteMenu(menuId: Long, eventId: Long) {
        val menu = menuRepository.findByIdAndEventId(menuId, eventId).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: $menuId")
        }
        menuRepository.delete(menu)
    }

    private fun makeMenuReadyForUpdate(menu: MenuCreateUpdateDTO, eventId: Long, menuItems: List<MenuItem>): Menu {
        val menuToSave = menuRepository.findById(menu.id!!).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: ${menu.id}")
        }
        menuToSave.name = menu.name!!
        menuToSave.event = eventService.getEventEntity(eventId)
        menuToSave.menuItems = menuItems
        return menuToSave
    }

}