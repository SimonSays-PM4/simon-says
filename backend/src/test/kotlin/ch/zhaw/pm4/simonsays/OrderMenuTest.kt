package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderMenuRepository
import ch.zhaw.pm4.simonsays.service.OrderMenuService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class OrderMenuTest {

    protected lateinit var orderMenuService: OrderMenuService

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuRepository: OrderMenuRepository

    @BeforeEach
    fun setup() {
        orderMenuRepository = mockk(relaxed = true)
        orderMenuService = OrderMenuService(orderMenuRepository)
    }

    @Test
    fun `Test Only Orders where everything is ready get returned`() {
        every { orderMenuRepository.findAllByOrderIdEquals(any()) } returns mutableListOf(
                getOrderMenu(
                        order = getOrder(),
                        orderMenuItems = mutableListOf(
                            getOrderMenuItem(
                                state = State.DONE,
                                order = getOrder(),
                                    orderIngredient = mutableListOf(
                                            getOrderIngredient(state = State.DONE)),
                            )
                        )
                ),
                getOrderMenu(
                        orderMenuItems = mutableListOf(
                                getOrderMenuItem(
                                        state = State.IN_PROGRESS,
                                        order = getOrder(),
                                        orderIngredient = mutableListOf(
                                                getOrderIngredient(state = State.IN_PROGRESS)),
                                )
                        )
                )
        )
        val expectedResponse = mutableListOf(
                getOrderMenu(
                        order = getOrder(),
                        orderMenuItems = mutableListOf(
                                getOrderMenuItem(
                                        state = State.IN_PROGRESS,
                                        order = getOrder()
                                )
                        )
                )
        )

        Assertions.assertEquals(expectedResponse, orderMenuService.getOrderMenus(1, 1))
    }


}