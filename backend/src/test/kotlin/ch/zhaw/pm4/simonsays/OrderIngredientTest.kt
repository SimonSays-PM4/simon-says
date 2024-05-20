package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.State
import ch.zhaw.pm4.simonsays.repository.OrderIngredientRepository
import ch.zhaw.pm4.simonsays.service.OrderIngredientService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderIngredientTest {

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientRepository: OrderIngredientRepository

    protected lateinit var orderIngredientService: OrderIngredientService

    @BeforeEach
    fun setup() {
        orderIngredientRepository = mockk(relaxed = true)
        orderIngredientService = OrderIngredientService(orderIngredientRepository)
    }

    @Test
    fun `test get orderIngredient by ingredient ids`() {
        every { orderIngredientRepository.findAllByIngredientIdInAndStateEquals(any(), State.IN_PROGRESS) } returns listOf(getOrderIngredient(state = State.DONE))
        Assertions.assertEquals(listOf(getOrderIngredient(state = State.DONE)), orderIngredientService.getOrderIngredientByIngredientIds(listOf(1)))
    }


}