package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MenuItemFactory(
        @Autowired private val menuItemRepository: MenuItemRepository
) {
    fun createMenuItem(name: String = "Default MenuItem Name"): MenuItem {
        val menuItem = MenuItem(name = name)
        return menuItemRepository.save(menuItem)
    }
}