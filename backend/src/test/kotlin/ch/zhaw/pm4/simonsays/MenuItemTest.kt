package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapperImpl
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.service.MenuItemService
import ch.zhaw.pm4.simonsays.service.MenuItemServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.Optional.empty

class MenuItemTest {
    @MockkBean(relaxed = true)
    protected lateinit var menuItemRepository: MenuItemRepository

    private lateinit var menuItemService: MenuItemService

    @BeforeEach
    fun setup() {
        // mockkStatic("kotlinx.coroutines.reactor.MonoKt")
        menuItemRepository = mockk(relaxed = true)
        menuItemService = MenuItemServiceImpl(menuItemRepository, MenuItemMapperImpl())
    }

    @Test
    fun `Test menu item creation`() {
        every { menuItemRepository.save(any()) } returns MenuItem(
                1,
                "MenuItem Test"
        )
        val menuItemCreateUpdateDto = MenuItemCreateUpdateDTO(
                null,
                "MenuItem Test",
        )
        Assertions.assertEquals(MenuItemDTO(
                1,
                "MenuItem Test",
        ), menuItemService.createUpdateMenuItem(menuItemCreateUpdateDto))
    }

    @Test
    fun `Test menu item fetching`() {
        every { menuItemRepository.findAll() } returns mutableListOf(
                MenuItem(
                        3,
                        "password",
                ),
                MenuItem(
                       2,
                        "password"
                )
        )
        val menuItems: List<MenuItemDTO> = menuItemService.getMenuItems()
        Assertions.assertEquals(2, menuItems.count())
    }

    @Test
    fun `Test menu item get`() {
        every { menuItemRepository.findById(1) } returns Optional.of(MenuItem(
                1,
                "testeventpassword"
        ))
        Assertions.assertEquals(
                MenuItemDTO(
                        1,
                        "testeventpassword",
                ), menuItemService.getMenuItem(1))
    }

    @Test
    fun `Test menu item got not found`() {
        every { menuItemRepository.findById(any()) } returns empty()
        Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { menuItemService.getMenuItem(1) },
                "Menu item not found with ID: 1"
        )
    }

    @Test
    fun `Test menu item deletion`() {
        every { menuItemRepository.findById(1) } returns Optional.of(MenuItem(
                3,
                "testeventpassword",
        ))
        Assertions.assertEquals(
                Unit, menuItemService.deleteMenuItem(1))
    }

    @Test
    fun `Test menu item deletion not found`() {
        every { menuItemRepository.findById(any()) } returns empty()
        Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { menuItemService.getMenuItem(1) },
                "Menu item not found with ID: 1"
        )
    }

}