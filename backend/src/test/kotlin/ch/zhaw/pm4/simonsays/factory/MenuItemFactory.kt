package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MenuItemFactory(
        @Autowired private val menuItemRepository: MenuItemRepository,
        @Autowired private val eventRepository: EventRepository
) {
    fun createMenuItem(
            name: String = "Default MenuItem Name",
            eventId: Long = 1,
            ingredients: List<Ingredient> = listOf(),
            price: Long = 1
    ): MenuItem {
        val event = eventRepository.findById(eventId).orElse(null) // Fetch the Event by ID
        val menuItem = MenuItem(name = name, event = event, ingredients = ingredients, price = price, menus = null) // Use the Event entity
        return menuItemRepository.save(menuItem)
    }
}