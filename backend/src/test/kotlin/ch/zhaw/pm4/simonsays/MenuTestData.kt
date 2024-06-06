package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.MenuCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Menu
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.entity.OrderMenu


fun getCreateUpdateMenuDTO(
    id: Long? = null,
    name: String? = "Menu Test",
    menuItemDTOs: List<MenuItemDTO>? = listOf(getMenuItemDTO()),
): MenuCreateUpdateDTO {
    return MenuCreateUpdateDTO(
        id,
        name,
        menuItemDTOs,
        null
    )
}
fun getMenu(
    id: Long = 1,
    name: String = "Menu Test",
    event: Event = getEvent(),
    menuItems: List<MenuItem> = listOf(getMenuItem()),
    price: Double = 0.0,
    orderMenu: Set<OrderMenu>? = null
): Menu {
    return Menu(
        id,
        name,
        event,
        menuItems,
        price = price,
        orderMenu = orderMenu
    )
}

fun getMenuDTO(
    id: Long = 1,
    name: String = "Menu Test",
    menuItemDTOs: List<MenuItemDTO> = listOf(getMenuItemDTO()),
    price: Double = 1.0
): MenuDTO {
    return MenuDTO(
        id,
        name,
        menuItemDTOs,
        price
    )
}