package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Menu
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.MenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MenuFactory(
    @Autowired private val menuRepository: MenuRepository,
    @Autowired private val eventRepository: EventRepository
) {
    fun createMenu(
        name: String = "Default Menu Name",
        eventId: Long = 1,
        menuItems: List<MenuItem> = listOf(),
        price: Long = 1
    ): Menu {
        val event = eventRepository.findById(eventId).orElse(null) // Fetch the Event by ID
        val menu = Menu(name = name, event = event, menuItems = menuItems) // Use the Event entity
        return menuRepository.save(menu)
    }
}