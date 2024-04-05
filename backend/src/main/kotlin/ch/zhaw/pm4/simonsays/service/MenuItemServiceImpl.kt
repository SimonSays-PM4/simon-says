package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import org.springframework.stereotype.Service

@Service
class MenuItemServiceImpl(
        private val menuItemRepository: MenuItemRepository,
        private val menuItemMapper: MenuItemMapper
) : MenuItemService {

    override fun getMenuItems(): MutableList<MenuItemDTO> {
        val menuItems: MutableList<MenuItem> = menuItemRepository.findAll()
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

        val menuItemToBeSaved = if(menuItem.id != null) {
            makeMenuItemReadyForUpdate(menuItem)
        } else {
            menuItemMapper.mapCreateDTOToMenuItem(menuItem)
        }

        val savedMenuItem = menuItemRepository.save(menuItemToBeSaved)
        return menuItemMapper.mapToMenuItemDTO(savedMenuItem)
    }

    private fun makeMenuItemReadyForUpdate(menuItem: MenuItemCreateUpdateDTO): MenuItem {
        val menuItemToSave = menuItemRepository.findById(menuItem.id!!).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: ${menuItem.id}")
        }
        menuItemToSave.name = menuItem.name!!
        return menuItemToSave
    }

    override fun deleteMenuItem(menuItemId: Long) {
        val menuItem = menuItemRepository.findById(menuItemId).orElseThrow {
            ResourceNotFoundException("Menu item not found with ID: $menuItemId")
        }
        menuItemRepository.delete(menuItem)
    }

}