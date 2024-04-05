package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO

interface MenuItemService {
    fun getMenuItems(): List<MenuItemDTO>
    fun getMenuItem(menuItemId: Long): MenuItemDTO
    fun createUpdateMenuItem(menuItem: MenuItemCreateUpdateDTO): MenuItemDTO
    fun deleteMenuItem(menuItemId: Long)
}