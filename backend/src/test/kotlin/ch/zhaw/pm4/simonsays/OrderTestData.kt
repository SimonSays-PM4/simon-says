package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*

fun getOrder(
    id: Long = 1,
    event: Event = getEvent(),
    menus: MutableSet<OrderMenu>? = mutableSetOf(),
    menuItems: MutableSet<OrderMenuItem>? = mutableSetOf(),
    totalPrice: Double = 15.20,
    state: State = State.IN_PROGRESS,
    tableNumber: Long? = 1,
    isTakeAway: Boolean = false
): FoodOrder {
    return FoodOrder(
        id = id,
        event = event,
        menus = menus,
        menuItems = menuItems,
        totalPrice = totalPrice,
        state = state,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}

fun getOrderCreateDTO(
    menus: List<MenuDTO>? = listOf(),
    menuItems: List<MenuItemDTO>? = listOf(),
    tableNumber: Long = 1,
    isTakeAway: Boolean = false
): OrderCreateDTO {
    return OrderCreateDTO(
        menus = menus,
        menuItems = menuItems,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}

fun getOrderDTO(
    id: Long = 1,
    menus: List<OrderMenuDTO>? = listOf(),
    menuItems: List<OrderMenuItemDTO>? = listOf(),
    totalPrice: Double = 15.20,
    state: State = State.IN_PROGRESS,
    tableNumber: Long = 1,
    isTakeAway: Boolean = false
): OrderDTO {
    return OrderDTO(
        id = id,
        menus = menus,
        menuItems = menuItems,
        totalPrice = totalPrice,
        state = state,
        tableNumber = tableNumber,
        isTakeAway = isTakeAway
    )
}

fun getOrderMenu(
    id: Long = 1,
    name: String = "Test order menu",
    event: Event = getEvent(),
    menu: Menu = getMenu(),
    orderMenuItem: MutableList<OrderMenuItem>,
    order: FoodOrder,
    state: State = State.IN_PROGRESS,
    price: Double = 15.20
): OrderMenu {
    return OrderMenu(
        id = id,
        name = name,
        event = event,
        menu = menu,
        orderMenuItems = orderMenuItem,
        order = order,
        state = state,
        price = price
    )
}

fun getOrderMenuDTO(
    id: Long = 1,
    name: String = "Test order menu",
    state: State = State.IN_PROGRESS,
    price: Double = 15.20,
    menuItems: List<OrderMenuItemDTO> = listOf(getOrderMenuItemDTO())
): OrderMenuDTO {
    return OrderMenuDTO(
        id = id,
        name = name,
        state = state,
        price = price,
        menuItems = menuItems
    )
}
fun getOrderMenuItem(
    id: Long = 1,
    name: String = "Test order menu item",
    event: Event = getEvent(),
    menuItem: MenuItem = getMenuItem(),
    orderIngredient: MutableList<OrderIngredient> = mutableListOf(
        getOrderIngredient()
    ),
    price: Double = 15.20,
    orderMenu: OrderMenu? = null,
    order: FoodOrder,
    state: State = State.IN_PROGRESS
): OrderMenuItem {
    return OrderMenuItem(
        id = id,
        name = name,
        event = event,
        menuItem = menuItem,
        orderIngredients = orderIngredient,
        price = price,
        orderMenu = orderMenu,
        order = order,
        state = state
    )
}

fun getOrderMenuItemDTO(
    id: Long = 1,
    name: String = "Test order menu item",
    state: State = State.IN_PROGRESS,
    ingredients: List<OrderIngredientDTO> = listOf(getOrderIngredientDTO()),
    price: Double = 15.20
): OrderMenuItemDTO {
    return OrderMenuItemDTO(
        id = id,
        name = name,
        state = state,
        ingredients = ingredients,
        price = price
    )
}

fun getOrderIngredient(
    id: Long = 1,
    name: String = "Order Ingredient Test",
    event: Event = getEvent(),
    ingredient: Ingredient = getTestIngredient1(),
    orderMenuItem: OrderMenuItem? = null,
    state: State = State.IN_PROGRESS
): OrderIngredient {
    return OrderIngredient(
        id = id,
        name = name,
        event = event,
        ingredient = ingredient,
        orderMenuItem = orderMenuItem,
        state = state
    )
}
fun getOrderIngredientDTO(
    id: Long = 1,
    name: String = "Order Ingredient Test",
    state: State = State.IN_PROGRESS
): OrderIngredientDTO {
    return OrderIngredientDTO(
        id = id,
        name = name,
        state = state
    )
}