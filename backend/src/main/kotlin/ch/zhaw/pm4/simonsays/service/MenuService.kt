package ch.zhaw.pm4.simonsays.service

import ch.zhaw.pm4.simonsays.api.types.MenuCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO


interface MenuService {
    fun listMenus(eventId: Long): List<MenuDTO>
    fun getMenu(menuId: Long, eventId: Long): MenuDTO
    fun createUpdateMenu(menu: MenuCreateUpdateDTO, eventId: Long): MenuDTO
    fun deleteMenu(menuId: Long, eventId: Long)
}