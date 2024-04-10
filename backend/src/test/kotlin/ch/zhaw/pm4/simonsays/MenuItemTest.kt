package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapperImpl
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.MenuItemService
import ch.zhaw.pm4.simonsays.service.MenuItemServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
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

    private lateinit var menuItemService: MenuItemService

    private lateinit var mockEvent: Event
    private lateinit var mockIngredient: Ingredient
    private lateinit var mockIngredientDTO: IngredientDTO

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        menuItemRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)

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

        // Setup Mock Ingredient
        mockIngredient = Ingredient(
                id = 1,
                name = "testingredient",
                event = mockEvent,
                menuItems = null
        )

        // Setup Mock Ingredient DTO
        mockIngredientDTO = IngredientDTO(
                id = 1,
                "testingredient"
        )

        // Construct the service with the mocked dependencies
        menuItemService = MenuItemServiceImpl(
                menuItemRepository,
                MenuItemMapperImpl(),
                eventService,
                ingredientRepository,
                ingredientMapper,
                eventMapper
        )
    }

    @Test
    fun `Test menu item creation`() {
        every { menuItemRepository.save(any()) } returns MenuItem(
                1,
                "MenuItem Test",
                mockEvent,
                listOf(
                    mockIngredient
                )

        )

        every { ingredientRepository.getReferenceById(any()) } returns Ingredient(
                mockIngredient.name,
                mockIngredient.id,
                mockIngredient.event,
                null
        )

        val menuItemCreateUpdateDto = MenuItemCreateUpdateDTO(
                null,
                1,
                "MenuItem Test",
                listOf(
                    mockIngredientDTO
                )
        )
        Assertions.assertEquals(MenuItemDTO(
                1,
                mockEvent.id!!,
                "MenuItem Test",
                listOf(
                    mockIngredientDTO
                )
        ), menuItemService.createUpdateMenuItem(menuItemCreateUpdateDto))
    }

    @Test
    fun `Test menu item fetching`() {
        every { menuItemRepository.findByEventId(mockEvent.id!!) } returns mutableListOf(
                MenuItem(
                        3,
                        "password",
                        mockEvent,
                        listOf(
                                mockIngredient
                        )
                ),
                MenuItem(
                       2,
                        "password",
                        mockEvent,
                        listOf(
                                mockIngredient
                        )
                )
        )
        val menuItems: List<MenuItemDTO> = menuItemService.getMenuItems(mockEvent.id!!)
        Assertions.assertEquals(2, menuItems.count())
    }

    @Test
    fun `Test menu item get`() {
        every { menuItemRepository.findById(1) } returns Optional.of(MenuItem(
                1,
                "testeventpassword",
                mockEvent,
                listOf(
                        mockIngredient
                )
        ))
        Assertions.assertEquals(
                MenuItemDTO(
                        1,
                        1,
                        "testeventpassword",
                        listOf(
                                mockIngredientDTO
                        )
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
                mockEvent,
                listOf(
                        mockIngredient
                )
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