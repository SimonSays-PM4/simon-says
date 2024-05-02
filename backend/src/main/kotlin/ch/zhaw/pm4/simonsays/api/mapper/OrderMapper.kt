package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface OrderMapper {
    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "event", source = "event"),
        Mapping(target = "menus", source = "menus"),
        Mapping(target = "menuItems", source = "menuItems"),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.stations", ignore = true),
        Mapping(target= "event.order", ignore = true),
        Mapping(target= "event.orderIngredient", ignore = true),
        Mapping(target= "event.orderMenuItem", ignore = true),
        Mapping(target= "event.orderMenu", ignore = true),
        Mapping(target = "isTakeAway", ignore = true)
    )
    fun mapOrderDtoToOrder(orderDto: OrderCreateDTO, event: EventDTO, menus: List<OrderMenu>, menuItems: List<OrderMenuItem>, totalPrice: Double, state: State = State.IN_PROGRESS): FoodOrder
    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "ingredient.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "ingredient", source = "ingredient"),
        Mapping(target= "orderMenuItem", ignore = true)
    )
    fun mapIngredientDtoToOrderIngredient(ingredientDTO: IngredientDTO, event: EventDTO, ingredient: Ingredient, state: State = State.IN_PROGRESS): OrderIngredient

    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "menuItem.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "menuItem", source = "menuItem"),
        Mapping(target= "price", source = "menuItem.price"),
        Mapping(target = "orderMenu", ignore = true),
        Mapping(target = "order", ignore = true),
    )
    fun mapMenuItemDtoToOrderMenuItem(menuItemDTO: MenuItemDTO, event: EventDTO, menuItem: MenuItem, orderIngredients: List<OrderIngredient> = listOf(), state: State = State.IN_PROGRESS): OrderMenuItem

    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "menu.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "menu", source = "menu"),
        Mapping(target= "price", source = "menuDTO.price"),
        Mapping(target= "order", ignore = true)
    )
    fun mapMenuDtoToOrderMenu(menuDTO: MenuDTO, event: EventDTO, menu: Menu, orderMenuItems: List<OrderMenuItem> = listOf(), state: State = State.IN_PROGRESS): OrderMenu

    @Mappings(
            Mapping(target= "isTakeAway", ignore = true)
    )
    fun mapOrderToOrderDTO(order: FoodOrder): OrderDTO

    @Mapping(target="menuItems", source="orderMenuItems")
    fun mapOrderMenuToOrderMenuDTO(orderMenu: OrderMenu): OrderMenuDTO
    fun mapOrderIngredientToOrderIngredientDTO(orderIngredient: OrderIngredient): OrderIngredientDTO
    @Mapping(target="ingredients", source="orderIngredients")
    fun mapOrderMenuItemToOrderMenuItemDTO(orderMenuItem: OrderMenuItem): OrderMenuItemDTO
}