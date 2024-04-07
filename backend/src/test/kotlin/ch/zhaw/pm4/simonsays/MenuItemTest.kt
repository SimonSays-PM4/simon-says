package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapperImpl
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.MenuItemService
import ch.zhaw.pm4.simonsays.service.MenuItemServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import java.util.*
import java.util.Optional.empty

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MenuItemTest {
    @MockkBean(relaxed = true)
    protected lateinit var menuItemRepository: MenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventService: EventService

    private lateinit var menuItemService: MenuItemService
    private lateinit var mockEvent: Event

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        menuItemRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)

        // Stubbing
        every { eventService.getEvent(any()) } returns EventDTO(
                name = "Testevent",
                password = "Testeventpassword",
                numberOfTables = 10,
                id = 1
        )

        // Setup Mock Event
        mockEvent = Event(
                id = 1, // Use whatever constructor parameters your Event class requires
                name = "Testevent",
                password = "Testeventpassword",
                numberOfTables = 10
        )

        // Construct the service with the mocked dependencies
        menuItemService = MenuItemServiceImpl(menuItemRepository, MenuItemMapperImpl(), eventService)
    }

    @Test
    fun `Test menu item creation`() {
        every { menuItemRepository.save(any()) } returns MenuItem(
                1,
                "MenuItem Test",
                mockEvent
        )
        val menuItemCreateUpdateDto = MenuItemCreateUpdateDTO(
                null,
                1,
                "MenuItem Test",
        )
        Assertions.assertEquals(MenuItemDTO(
                1,
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
                        mockEvent
                ),
                MenuItem(
                       2,
                        "password",
                        mockEvent
                )
        )
        val menuItems: List<MenuItemDTO> = menuItemService.getMenuItems()
        Assertions.assertEquals(2, menuItems.count())
    }

    @Test
    fun `Test menu item get`() {
        every { menuItemRepository.findById(1) } returns Optional.of(MenuItem(
                1,
                "testeventpassword",
                mockEvent
        ))
        Assertions.assertEquals(
                MenuItemDTO(
                        1,
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
                mockEvent
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