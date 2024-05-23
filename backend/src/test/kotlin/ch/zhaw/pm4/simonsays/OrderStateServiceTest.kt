package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapperImpl
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientUpdateDTO
import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.exception.ValidationException
import ch.zhaw.pm4.simonsays.repository.*
import ch.zhaw.pm4.simonsays.service.OrderStateService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OrderStateServiceTest {

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuItemRepository: OrderMenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMapper: OrderMapper

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientRepository: OrderIngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderRepository: OrderRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuRepository: OrderMenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var stationViewNamespace: StationViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var assemblyViewNamespace: AssemblyViewNamespace

    private lateinit var orderStateService: OrderStateService

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        ingredientRepository = mockk(relaxed = true)
        orderMapper = mockk(relaxed = true)
        orderIngredientRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        orderMenuItemRepository = mockk(relaxed = true)
        orderMenuRepository = mockk(relaxed = true)
        stationViewNamespace = mockk(relaxed = true)
        assemblyViewNamespace = mockk(relaxed = true)

        orderStateService = OrderStateService(
                ingredientRepository,
                orderRepository,
                orderMenuItemRepository,
                orderMenuRepository,
                orderIngredientRepository,
                assemblyViewNamespace,
                stationViewNamespace,
                OrderMapperImpl()
        )
    }

    @Test
    fun `Test mark ingredient as produced`() {
        val orderIngredientDTODone = getOrderIngredientDTO(
                state = State.DONE
        )
        every { ingredientRepository.findAllByStationsIdAndEventId(any(), any()) } returns listOf(
                getTestIngredient1()
        )
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderIngredient())
        every { orderIngredientRepository.save(any()) } returns getOrderIngredient(state = State.DONE)
        Assertions.assertEquals(
                orderIngredientDTODone,
                orderStateService.processIngredient(1, 1, OrderIngredientUpdateDTO(1, "Test", State.IN_PROGRESS))
        )
    }

    @Test
    fun `Test throw exception when station updates out of scope order ingredient`() {
        val orderIngredientUpdateDTO = OrderIngredientUpdateDTO(1, "Test", State.IN_PROGRESS)
        every { ingredientRepository.findAllByStationsIdAndEventId(any(), any()) } returns listOf()
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderIngredient())
        Assertions.assertThrows(
                ValidationException::class.java,
                { orderStateService.processIngredient(1, 1, OrderIngredientUpdateDTO(1, "Test", State.IN_PROGRESS)) },
                "This station is not allowed to update the state of the ingredient with the id: ${orderIngredientUpdateDTO.id} (${orderIngredientUpdateDTO.name})"
        )
    }

    @Test
    fun `Test throw exception when station updates invalid order ingredient`() {
        val orderIngredientUpdateDTO = OrderIngredientUpdateDTO(1, "Test", State.IN_PROGRESS)
        every { ingredientRepository.findAllByStationsIdAndEventId(any(), any()) } returns listOf(getTestIngredient1())
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { orderStateService.processIngredient(1, 1, orderIngredientUpdateDTO) },
                "No order ingredient found with the ID: ${orderIngredientUpdateDTO.id}"
        )
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
        Assertions.assertEquals(getOrderMenuDTO(state = State.DONE), orderStateService.updateOrderMenuState(1, 1))
    }

    @Test
    fun `test update order menu state not found`() {
        every { orderMenuRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderStateService.updateOrderMenuState(1, 1)
        }
        Assertions.assertEquals("OrderMenu not found with ID: 1", error.message)
    }

    @Test
    fun `test update order menu item state`() {
        every { orderMenuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderMenuItem(order = getOrder()))
        every { orderMenuItemRepository.save(any()) } returns getOrderMenuItem(order = getOrder(), state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderMenuItemDTO(state = State.DONE), orderStateService.updateOrderMenuItemState(1, 1))
    }

    @Test
    fun `test update order menu item state not found`() {
        every { orderMenuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderStateService.updateOrderMenuItemState(1, 1)
        }
        Assertions.assertEquals("OrderMenuItem not found with ID: 1", error.message)
    }

    @Test
    fun `test update order ingredient state`() {
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderIngredient())
        every { orderIngredientRepository.save(any()) } returns getOrderIngredient(state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderIngredientDTO(state = State.DONE), orderStateService.updateOrderIngredientState(1, 1))
    }

    @Test
    fun `test update order ingredient state not found`() {
        every { orderIngredientRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderStateService.updateOrderIngredientState(1, 1)
        }
        Assertions.assertEquals("OrderIngredient not found with ID: 1", error.message)
    }

    @Test
    fun `test update order menu state order not found`() {
        every { orderMenuRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(
                getOrderMenuItem(order = getOrder()))
        ))
        every { orderMenuRepository.save(any()) } returns getOrderMenu(order = getOrder(), orderMenuItems = mutableListOf(
                getOrderMenuItem(order = getOrder())), state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.empty()
        val error = Assertions.assertThrows(ResourceNotFoundException::class.java) {
            orderStateService.updateOrderMenuState(1, 1)
        }
        Assertions.assertEquals("Order not found with ID: 1", error.message)
    }

    @Test
    fun `check if order update is required when menu item is checked`() {
        every { orderMenuItemRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getOrderMenuItem(order = null))
        every { orderMenuItemRepository.save(any()) } returns getOrderMenuItem(order = null, orderMenu = getOrderMenu(), state = State.DONE)
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder()
        Assertions.assertEquals(getOrderMenuItemDTO(state = State.DONE), orderStateService.updateOrderMenuItemState(1, 1))
    }
}