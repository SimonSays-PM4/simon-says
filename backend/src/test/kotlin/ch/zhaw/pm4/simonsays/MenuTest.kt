package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuMapperImpl
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.MenuRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.MenuItemService
import ch.zhaw.pm4.simonsays.service.MenuService
import ch.zhaw.pm4.simonsays.service.MenuServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MenuTest {
    @MockkBean(relaxed = true)
    protected lateinit var menuRepository: MenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventService: EventService

    @MockkBean(relaxed = true)
    protected lateinit var eventMapper: EventMapper

    @MockkBean(relaxed = true)
    protected lateinit var menuItemRepository: MenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var menuItemMapper: MenuItemMapper

    @MockkBean(relaxed = true)
    protected lateinit var menuItemService: MenuItemService

    private lateinit var menuService: MenuService

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        menuRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        menuItemRepository = mockk(relaxed = true)
        menuItemMapper = mockk(relaxed = true)
        menuItemService = mockk(relaxed = true)
        eventMapper = mockk(relaxed = true)

        // Construct the service with the mocked dependencies
        menuService = MenuServiceImpl(
            menuRepository,
            MenuMapperImpl(),
            eventService,
            menuItemRepository
        )
    }

    @Test
    fun `Test menu creation`() {
        every { menuRepository.save(any()) } returns getMenu(menuItems = listOf(getMenuItem(), getMenuItem(id = 2, price = 3)))

        every { menuItemRepository.getReferenceById(any()) } returns getMenuItem()

        val menuCreateUpdateDto = getCreateUpdateMenuDTO()
        Assertions.assertEquals(getMenuDTO(menuItemDTOs = listOf(getMenuItemDTO(), getMenuItemDTO(id = 2, price = 3))), menuService.createUpdateMenu(menuCreateUpdateDto, getEvent().id!!))
    }

    @Test
    fun `Test menu update`()  {
        every { menuRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getMenu())
        every { menuRepository.save(any()) } returns getMenu(name = "updated name")
        val menuCreateUpdateDto = getCreateUpdateMenuDTO(name = "updated name")
        Assertions.assertEquals(getMenuDTO(name = "updated name"), menuService.createUpdateMenu(menuCreateUpdateDto, getEvent().id!!))
    }

    @Test
    fun `Test menu fetching`() {
        every { menuRepository.findAllByEventId(getEvent().id!!) } returns mutableListOf(
            getMenu(),
            getMenu()
        )
        val menus: List<MenuDTO> = menuService.listMenus(getEvent().id!!)
        Assertions.assertEquals(2, menus.count())
    }

    @Test
    fun `Test menu get`() {
        every { menuRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenu())
        Assertions.assertEquals(
            getMenuDTO(), menuService.getMenu(1, getEvent().id!!))
    }

    @Test
    fun `Test menu got not found`() {
        every { menuRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { menuService.getMenu(1, getEvent().id!!) },
            "Menu not found with ID: 1"
        )
    }

    @Test
    fun `Test menu deletion`() {
        every { menuRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenu())
        Assertions.assertEquals(
            Unit, menuService.deleteMenu(1, getEvent().id!!))
    }

    @Test
    fun `Test menu deletion not found`() {
        every { menuRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { menuService.getMenu(1, getEvent().id!!) },
            "Menu not found with ID: 1"
        )
    }
}