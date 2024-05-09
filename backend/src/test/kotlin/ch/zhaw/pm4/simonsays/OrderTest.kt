package ch.zhaw.pm4.simonsays

//import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapperImpl
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.*
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.OrderService
import ch.zhaw.pm4.simonsays.service.printer.PrinterService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class OrderTest {

    @MockkBean(relaxed = true)
    protected lateinit var orderRepository: OrderRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientRepository: OrderIngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuItemRepository: OrderMenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuRepository: OrderMenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventService: EventService

    @MockkBean(relaxed = true)
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var menuItemRepository: MenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var menuRepository: MenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var printerService: PrinterService

    @MockkBean(relaxed = true)
    protected lateinit var stationViewNamespace: StationViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var assemblyViewNamespace: AssemblyViewNamespace

    protected lateinit var orderService: OrderService

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        orderRepository = mockk(relaxed = true)
        orderIngredientRepository = mockk(relaxed = true)
        orderMenuItemRepository = mockk(relaxed = true)
        orderMenuRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)
        menuItemRepository = mockk(relaxed = true)
        menuRepository = mockk(relaxed = true)
        printerService = mockk(relaxed = true)
        stationViewNamespace = mockk(relaxed = true)
        assemblyViewNamespace = mockk(relaxed = true)

        // Construct the service with the mocked dependencies
        orderService = OrderService(
                OrderMapperImpl(),
                orderRepository,
                orderIngredientRepository,
                orderMenuRepository,
                orderMenuItemRepository,
                eventService,
                ingredientRepository,
                menuItemRepository,
                menuRepository,
                printerService,
                stationViewNamespace,
                assemblyViewNamespace
        )
    }

    @Test
    fun `test list orders`() {
      every { orderRepository.findAllByEventId(any()) } returns listOf(getOrder(), getOrder())
        Assertions.assertEquals(2, orderService.listOrders(1).count())
    }

    @Test
    fun `test empty list orders`() {
        every { orderRepository.findAllByEventId(any()) } returns listOf()
        Assertions.assertEquals(0, orderService.listOrders(1).count())
    }

    @Test
    fun `test delete order`() {
        every { orderRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrder())
        Assertions.assertEquals(Unit, orderService.deleteOrder(1, 1))
    }

    @Test
    fun `test delete order not found`() {
        every { orderRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.deleteOrder(1, 1)
        }
        Assertions.assertEquals("Order not found with ID: 1", error.message)
    }

    @Test
    fun `test create order`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        every { orderRepository.save(any()) } returns getOrder(menus = mutableListOf(getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(getOrderMenuItem(order = getOrder())))))

        val orderCreateDTO = getOrderCreateDTO(menus = listOf(getMenuDTO(menuItemDTOs = listOf(getMenuItemDTO(ingredientDTOs = listOf(getTestIngredientDTO()))))))
        Assertions.assertEquals(getOrderDTO(menus = listOf(getOrderMenuDTO(menuItems = listOf(getOrderMenuItemDTO())))), orderService.createOrder(orderCreateDTO, 1))
    }

    @Test
    fun `test create order no menu or menuItem`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        every { orderRepository.save(any()) } returns getOrder()

        val orderCreateDTO = getOrderCreateDTO()
        val error = Assertions.assertThrows(ValidationException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Order must have at least one menu or menu item", error.message)
    }

    @Test
    fun `test create order table number is zero`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(tableNumber = 0)
        val error = Assertions.assertThrows(ValidationException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Table number must be between 1 and 1", error.message)
    }

    @Test
    fun `test create order table number is greater than event table numbers`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(tableNumber = 99)
        val error = Assertions.assertThrows(ValidationException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Table number must be between 1 and 1", error.message)
    }

    @Test
    fun `test create order menu has no menuItems`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(menus = listOf(getMenuDTO(menuItemDTOs = listOf())))
        val error = Assertions.assertThrows(ValidationException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Menu must have at least one menu item", error.message)
    }

    @Test
    fun `test create order menuItems has no ingredients`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(menuItems = listOf(getMenuItemDTO(ingredientDTOs = listOf())))
        val error = Assertions.assertThrows(ValidationException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Menu item must have at least one ingredient", error.message)
    }

    @Test
    fun `test create order menu not found`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(menus = listOf(getMenuDTO(id = 2)))
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Menu not found with ID: 2", error.message)
    }

    @Test
    fun `test create order menuItem not found`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(menuItems = listOf(getMenuItemDTO(id = 2)))
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("MenuItem not found with ID: 2", error.message)
    }

    @Test
    fun `test create order ingredient not found`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        val orderCreateDTO = getOrderCreateDTO(menuItems = listOf(getMenuItemDTO(ingredientDTOs = listOf(
            getTestIngredientDTO(id = 2)
        ))))
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.createOrder(orderCreateDTO, 1)
        }
        Assertions.assertEquals("Ingredient not found with ID: 2", error.message)
    }

    @Test
    fun `test update order menu state`() {
        every { orderMenuRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(
            getOrderMenuItem(order = getOrder()))
        ))
        every { orderMenuRepository.save(any()) } returns getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(
            getOrderMenuItem(order = getOrder())), state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderMenuDTO(state = State.DONE), orderService.updateOrderMenuState(1, 1))
    }

    @Test
    fun `test update order menu state not found`() {
        every { orderMenuRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.updateOrderMenuState(1, 1)
        }
        Assertions.assertEquals("OrderMenu not found with ID: 1", error.message)
    }

    @Test
    fun `test update order menu item state`() {
        every { orderMenuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderMenuItem(order = getOrder()))
        every { orderMenuItemRepository.save(any()) } returns getOrderMenuItem(order = getOrder(), state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderMenuItemDTO(state = State.DONE), orderService.updateOrderMenuItemState(1, 1))
    }

    @Test
    fun `test update order menu item state not found`() {
        every { orderMenuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.updateOrderMenuItemState(1, 1)
        }
        Assertions.assertEquals("OrderMenuItem not found with ID: 1", error.message)
    }

    @Test
    fun `test update order ingredient state`() {
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderIngredient())
        every { orderIngredientRepository.save(any()) } returns getOrderIngredient(state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderIngredientDTO(state = State.DONE), orderService.updateOrderIngredientState(1, 1))
    }

    @Test
    fun `test update order ingredient state not found`() {
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderService.updateOrderIngredientState(1, 1)
        }
        Assertions.assertEquals("OrderIngredient not found with ID: 1", error.message)
    }

    @Test
    fun `test get orderIngredient by ingredient ids`() {
        every { orderIngredientRepository.findAllByIngredientIdInAndStateEquals(any(), State.IN_PROGRESS) } returns listOf(getOrderIngredient())
        Assertions.assertEquals(1, orderService.getOrderIngredientByIngredientIds(listOf(1)).count())
    }

    @Test
    fun `test order triggers printer`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { menuRepository.findAllByEventId(any()) } returns listOf(getMenu())
        every { menuItemRepository.findAllByEventId(any()) } returns listOf(getMenuItem())
        every { ingredientRepository.findAllByEventId(any()) } returns listOf(getTestIngredient1())

        every { orderRepository.save(any()) } returns getOrder(menus = mutableListOf(getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(getOrderMenuItem(order = getOrder())))))

        val orderCreateDTO = getOrderCreateDTO(menus = listOf(getMenuDTO(menuItemDTOs = listOf(getMenuItemDTO(ingredientDTOs = listOf(getTestIngredientDTO()))))))
        orderService.createOrder(orderCreateDTO, 1)
        verify { printerService.printFoodOrder(any()) }
    }
}