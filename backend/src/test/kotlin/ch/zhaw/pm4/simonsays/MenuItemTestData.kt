package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.*


fun getCreateUpdateMenuItemDTO(
    id: Long? = null,
    name: String? = "MenuItem Test",
    ingredients: List<IngredientDTO>? = listOf(getTestIngredientDTO()),
    price: Double = 1.0
): MenuItemCreateUpdateDTO {
    return MenuItemCreateUpdateDTO(
        id,
        name,
        ingredients,
        price
    )
}

fun getMenuItem(
    id: Long = 1,
    name: String = "MenuItem Test",
    event: Event = getEvent(),
    ingredients: List<Ingredient> = listOf(getTestIngredient1()),
    menus: List<Menu>? = null,
    price: Double = 1.0,
    orderMenuItems: Set<OrderMenuItem>? = null,
): MenuItem {
    return MenuItem(
        id,
        name,
        event,
        ingredients,
        price,
        menus,
        orderMenuItems
    )
}

fun getMenuItemDTO(
    id: Long = 1,
    name: String = "MenuItem Test",
    ingredientDTOs: List<IngredientDTO> = listOf(getTestIngredientDTO()),
    price: Double = 1.0
): MenuItemDTO {
    return MenuItemDTO(
        id,
        name,
        ingredientDTOs,
        price
    )
}