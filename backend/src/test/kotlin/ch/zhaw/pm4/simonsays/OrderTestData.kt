package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import org.springframework.http.HttpStatus

fun getOrder(
    id: Long = 1,
    event: Event = getEvent(),
    menus: MutableList<OrderMenu>? = mutableListOf(),
    menuItems: MutableList<OrderMenuItem>? = mutableListOf(),
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
    tableNumber: Long? = 1,
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
    tableNumber: Long? = 1,
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

fun getOrderDTOCreated1() = getOrderDTO(
    menus = mutableListOf(
        getOrderMenuDTO(
            name = "Test Menu Name",
            price = 1.0,
            menuItems = mutableListOf(
                getOrderMenuItemDTO(
                    id = 2,
                    name = "Test Menu Item Name",
                    price = 1.0,
                    ingredients = mutableListOf(
                        getOrderIngredientDTO(id = 2)
                    )
                )
            )
        )
    ),
    menuItems = mutableListOf(
        getOrderMenuItemDTO(price = 1.0, name = "Test Menu Item Name")
    ),
    totalPrice = 2.0,
)


fun getOrderDTOCreated2() =
    getOrderDTO(
        id = 2,
        menus = mutableListOf(
            getOrderMenuDTO(
                id = 2,
                name = "Test Menu Name",
                price = 1.0,
                menuItems = mutableListOf(
                    getOrderMenuItemDTO(
                        id = 4,
                        name = "Test Menu Item Name",
                        price = 1.0,
                        ingredients = mutableListOf(
                            getOrderIngredientDTO(id = 4)
                        )
                    )
                )
            )
        ),
        menuItems = mutableListOf(
            getOrderMenuItemDTO(
                id = 3,
                price = 1.0,
                name = "Test Menu Item Name",
                ingredients = mutableListOf(getOrderIngredientDTO(id = 3))
            )
        ),
        totalPrice = 2.0,
        tableNumber = null,
        isTakeAway = true
    )


fun getNotFoundError(type: String, id: Long) = ErrorMessageModel(
    HttpStatus.NOT_FOUND.value(),
    "${type} not found with ID: ${id}",
    null
)

