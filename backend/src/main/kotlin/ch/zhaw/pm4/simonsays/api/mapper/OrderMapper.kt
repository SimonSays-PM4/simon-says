package ch.zhaw.pm4.simonsays.api.mapper

import ch.zhaw.pm4.simonsays.api.types.*
import ch.zhaw.pm4.simonsays.entity.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring")
interface OrderMapper {
    @Mappings(
        Mapping(target = "id", ignore = true),
        Mapping(target = "event", source = "event"),
        Mapping(target = "menus", source = "menus"),
        Mapping(target = "menuItems", source = "menuItems"),
    )
    fun mapOrderDtoToOrder(orderDto: OrderCreateDTO, event: EventDTO, menus: Set<OrderMenu>, menuItems: Set<OrderMenuItem>, totalPrice: Double, state: State = State.IN_PROGRESS): FoodOrder
    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "ingredientDTO.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "ingredient", source = "ingredient")
    )
    fun mapIngredientDtoToOrderIngredient(ingredientDTO: IngredientDTO, event: EventDTO, ingredient: Ingredient, state: State = State.IN_PROGRESS): OrderIngredient

    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "menuItemDTO.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "menuItem", source = "menuItem"),
        Mapping(target= "price", source = "menuItemDTO.price"),
    )
    fun mapMenuItemDtoToOrderMenuItem(menuItemDTO: MenuItemDTO, event: EventDTO, menuItem: MenuItem, orderIngredients: Set<OrderIngredient>, state: State = State.IN_PROGRESS): OrderMenuItem

    @Mappings(
        Mapping(target= "id", ignore = true),
        Mapping(target = "name", source = "menuDTO.name"),
        Mapping(target= "event.menus", ignore = true),
        Mapping(target= "event.menuItems", ignore = true),
        Mapping(target= "event.ingredients", ignore = true),
        Mapping(target= "menu", source = "menu"),
        Mapping(target= "price", source = "menuDTO.price"),
    )
    fun mapMenuDtoToOrderMenu(menuDTO: MenuDTO, event: EventDTO, menu: Menu, orderMenuItems: Set<OrderMenuItem>, state: State = State.IN_PROGRESS): OrderMenu

    fun mapOrderToOrderDTO(order: FoodOrder): OrderDTO
}