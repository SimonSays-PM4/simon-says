package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO

interface MenuItemService {
    fun listMenuItems(eventId: Long): List<MenuItemDTO>
    fun getMenuItem(menuItemId: Long, eventId: Long): MenuItemDTO
    fun createUpdateMenuItem(menuItem: MenuItemCreateUpdateDTO, eventId: Long): MenuItemDTO
    fun deleteMenuItem(menuItemId: Long, eventId: Long)
}