package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapperImpl
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.IngredientService
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

    @MockkBean(relaxed = true)
    protected lateinit var eventMapper: EventMapper

    @MockkBean(relaxed = true)
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var ingredientMapper: IngredientMapper

    @MockkBean(relaxed = true)
    protected lateinit var ingredientService: IngredientService

    private lateinit var menuItemService: MenuItemService

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        menuItemRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)
        ingredientMapper = mockk(relaxed = true)
        ingredientService = mockk(relaxed = true)
        eventMapper = mockk(relaxed = true)

        // Construct the service with the mocked dependencies
        menuItemService = MenuItemServiceImpl(
                menuItemRepository,
                MenuItemMapperImpl(),
                eventService,
                ingredientRepository
        )
    }

    @Test
    fun `Test menu item creation`() {
        every { menuItemRepository.save(any()) } returns getMenuItem()

        every { ingredientRepository.getReferenceById(any()) } returns getTestIngredient1()

        val menuItemCreateUpdateDto = getCreateUpdateMenuItemDTO()
        Assertions.assertEquals(getMenuItemDTO(), menuItemService.createUpdateMenuItem(menuItemCreateUpdateDto, getEvent().id!!))
    }

    @Test
    fun `Test menu item update`()  {
        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getMenuItem())
        every { menuItemRepository.save(any()) } returns getMenuItem(name = "updated name")
        val menuItemCreateUpdateDto = getCreateUpdateMenuItemDTO(name = "updated name")
        Assertions.assertEquals(getMenuItemDTO(name = "updated name"), menuItemService.createUpdateMenuItem(menuItemCreateUpdateDto, getEvent().id!!))
    }

    @Test
    fun `Test menu item fetching`() {
        every { menuItemRepository.findAllByEventId(getEvent().id!!) } returns mutableListOf(
                getMenuItem(),
                getMenuItem()
        )
        val menuItems: List<MenuItemDTO> = menuItemService.listMenuItems(getEvent().id!!)
        Assertions.assertEquals(2, menuItems.count())
    }

    @Test
    fun `Test menu item get`() {
        every { menuItemRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenuItem())
        Assertions.assertEquals(
                getMenuItemDTO(), menuItemService.getMenuItem(1, getEvent().id!!))
    }

    @Test
    fun `Test menu item got not found`() {
        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java
        ) { menuItemService.getMenuItem(1, getEvent().id!!) }
        Assertions.assertEquals("Menu item not found with ID: 1", error.message)
    }

    @Test
    fun `Test menu item deletion`() {
        every { menuItemRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenuItem())
        Assertions.assertEquals(
                Unit, menuItemService.deleteMenuItem(1, getEvent().id!!))
    }

    @Test
    fun `Test menu item deletion not found`() {
        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java)
        { menuItemService.getMenuItem(1, getEvent().id!!) }
        Assertions.assertEquals("Menu item not found with ID: 1", error.message)
    }

}