//package ch.zhaw.pm4.simonsays
//
//import ch.zhaw.pm4.simonsays.api.mapper.*
//import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
//import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
//import ch.zhaw.pm4.simonsays.repository.IngredientRepository
//import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
//import ch.zhaw.pm4.simonsays.repository.MenuRepository
//import ch.zhaw.pm4.simonsays.service.*
//import com.ninjasquad.springmockk.MockkBean
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import java.util.*
//
//class MenuTest {
//    @MockkBean(relaxed = true)
//    protected lateinit var menuRepository: MenuRepository
//
//    @MockkBean(relaxed = true)
//    protected lateinit var eventService: EventService
//
//    @MockkBean(relaxed = true)
//    protected lateinit var eventMapper: EventMapper
//
//    @MockkBean(relaxed = true)
//    protected lateinit var menuItemRepository: MenuItemRepository
//
//    @MockkBean(relaxed = true)
//    protected lateinit var menuItemMapper: MenuItemMapper
//
//    @MockkBean(relaxed = true)
//    protected lateinit var menuItemService: MenuItemService
//
//    private lateinit var menuService: MenuService
//
//    @BeforeEach
//    fun setup() {
//        // Initialization of mocks
//        menuRepository = mockk(relaxed = true)
//        eventService = mockk(relaxed = true)
//        menuItemRepository = mockk(relaxed = true)
//        menuItemMapper = mockk(relaxed = true)
//        menuItemService = mockk(relaxed = true)
//        eventMapper = mockk(relaxed = true)
//
//        // Construct the service with the mocked dependencies
//        menuItemService = MenuServiceImpl(
//            menuRepository,
//            MenuMapperImpl(),
//            eventService,
//            menuItemRepository
//        )
//    }
//
//    @Test
//    fun `Test menu creation`() {
//        every { menuRepository.save(any()) } returns getMenu()
//
//        every { menuItemRepository.getReferenceById(any()) } returns getMenuItem()
//
//        val menuCreateUpdateDto = getCreateUpdateMenuDTO()
//        Assertions.assertEquals(getMenuDTO(), menuService.createUpdateMenu(menuCreateUpdateDto, getEvent().id!!))
//    }
//
//    @Test
//    fun `Test menu update`()  {
//        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getMenuItem())
//        every { menuItemRepository.save(any()) } returns getMenuItem(name = "updated name")
//        val menuItemCreateUpdateDto = getCreateUpdateMenuItemDTO(name = "updated name")
//        Assertions.assertEquals(getMenuItemDTO(name = "updated name"), menuItemService.createUpdateMenuItem(menuItemCreateUpdateDto, getEvent().id!!))
//    }
//
//    @Test
//    fun `Test menu fetching`() {
//        every { menuItemRepository.findAllByEventId(getEvent().id!!) } returns mutableListOf(
//            getMenuItem(),
//            getMenuItem()
//        )
//        val menuItems: List<MenuItemDTO> = menuItemService.listMenuItems(getEvent().id!!)
//        Assertions.assertEquals(2, menuItems.count())
//    }
//
//    @Test
//    fun `Test menu get`() {
//        every { menuItemRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenuItem())
//        Assertions.assertEquals(
//            getMenuItemDTO(), menuItemService.getMenuItem(1, getEvent().id!!))
//    }
//
//    @Test
//    fun `Test menu got not found`() {
//        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
//        Assertions.assertThrows(
//            ResourceNotFoundException::class.java,
//            { menuItemService.getMenuItem(1, getEvent().id!!) },
//            "Menu item not found with ID: 1"
//        )
//    }
//
//    @Test
//    fun `Test menu deletion`() {
//        every { menuItemRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getMenuItem())
//        Assertions.assertEquals(
//            Unit, menuItemService.deleteMenuItem(1, getEvent().id!!))
//    }
//
//    @Test
//    fun `Test menu deletion not found`() {
//        every { menuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
//        Assertions.assertThrows(
//            ResourceNotFoundException::class.java,
//            { menuItemService.getMenuItem(1, getEvent().id!!) },
//            "Menu item not found with ID: 1"
//        )
//    }
//}