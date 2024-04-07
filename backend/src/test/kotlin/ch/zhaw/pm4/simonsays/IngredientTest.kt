package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapperImpl
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.IngredientService
import ch.zhaw.pm4.simonsays.service.IngredientServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.Optional.empty

class IngredientTest {
    @MockkBean(relaxed = true)
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventService: EventService

    private lateinit var ingredientService: IngredientService

    @BeforeEach
    fun setup() {
        ingredientRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        ingredientService = IngredientServiceImpl(ingredientRepository, IngredientMapperImpl(), eventService)
    }

    @Test
    fun `Test ingredient creation`() {
        every { eventService.getEvent(any()) } returns getEventDTO()
        every { ingredientRepository.save(any()) } returns getIngredient1()
        val ingredientCreateDTO = createUpdateIngredientDTO()
        Assertions.assertEquals(
            getIngredient1DTO(), ingredientService.createUpdateIngredient(ingredientCreateDTO, 1)
        )
    }

    @Test
    fun `Test ingredient list`() {
        every { ingredientRepository.findAllByEventId(any()) } returns
                listOf(Ingredient("Testingredient", 1, getEvent()), Ingredient("Testingredient2", 2, getEvent()))

        Assertions.assertEquals(
            listOf(IngredientDTO(1, "Testingredient"), IngredientDTO(2, "Testingredient2")),
            ingredientService.listIngredients(1)
        )
    }

    @Test
    fun `Test ingredient get`() {
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getIngredient1())
        Assertions.assertEquals(
            getIngredient1DTO(), ingredientService.getIngredient(1,1)
        )
    }

    @Test
    fun `Test ingredient get not found`() {
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.getIngredient(1,1) },
            "Ingredient not found with ID: 1"
        )
    }

    @Test
    fun `Test ingredient deletion`() {
        every { ingredientRepository.delete(any()) } returns Unit
        Assertions.assertEquals(
            Unit, ingredientService.deleteIngredient(1,1)
        )
    }

    @Test
    fun `Test ingredient deletion not found`() {
        every { eventService.getEvent(any()) } returns getEventDTO()
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.getIngredient(1,1) },
            "Ingredient not found with ID: 1"
        )
    }

    @Test
    fun `Test ingredient update`() {
        every { eventService.getEvent(any()) } returns getEventDTO()
        every { ingredientRepository.save(any()) } returns getIngredient1("TestingredientUpdated")
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getIngredient1())
        val ingredientCreateUpdateDTO = createUpdateIngredientDTO(1, "TestingredientUpdated")
        Assertions.assertEquals(
            IngredientDTO(
                1, "TestingredientUpdated"
            ), ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 1)
        )
    }

    @Test
    fun `Test ingredient update not found`() {
        every { eventService.getEvent(any()) } returns getEventDTO()
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        val ingredientCreateUpdateDTO = createUpdateIngredientDTO(2)

        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 1) },
            "Ingredient not found with ID: 2"
        )
    }

    @Test
    fun `Test ingredient update not found event`() {
        every { eventService.getEvent(any()) } throws ResourceNotFoundException("Event not found with ID: 404")
        val ingredientCreateUpdateDTO = createUpdateIngredientDTO(2)

        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 404) },
            "Event not found with ID: 2"
        )
    }
}