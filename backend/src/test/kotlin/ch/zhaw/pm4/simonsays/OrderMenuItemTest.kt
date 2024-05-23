package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderMenuItemRepository
import ch.zhaw.pm4.simonsays.service.OrderMenuItemService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderMenuItemTest {

    protected lateinit var orderMenuItemService: OrderMenuItemService

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuItemRepository: OrderMenuItemRepository

    @BeforeEach
    fun setup() {
        orderMenuItemRepository = mockk(relaxed = true)
        orderMenuItemService = OrderMenuItemService(orderMenuItemRepository)
    }

    @Test
    fun `test get orderMenuItems by order and event`(){
        every { orderMenuItemRepository.findAllByOrderIdEqualsAndOrderMenuEquals(any(),null) } returns mutableListOf(
                getOrderMenuItem(
                        orderIngredient = mutableListOf(
                                getOrderIngredient(state = State.DONE)),
                        order = getOrder()
                ),
                getOrderMenuItem(
                        order = getOrder()
                )
        )
        Assertions.assertEquals(1, orderMenuItemService.getOrderMenuItems(1, 1).count())
    }


}