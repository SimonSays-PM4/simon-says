package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapperImpl
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.exception.ResourceInUseException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.service.EventService
import ch.zhaw.pm4.simonsays.service.IngredientService
import ch.zhaw.pm4.simonsays.service.StationService
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

    @MockkBean(relaxed = true)
    protected lateinit var stationService: StationService

    private lateinit var ingredientService: IngredientService

    @BeforeEach
    fun setup() {
        ingredientRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        stationService = mockk(relaxed = true)
        ingredientService = IngredientService(ingredientRepository, IngredientMapperImpl(), eventService, stationService)
    }

    @Test
    fun `Test ingredient creation`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { ingredientRepository.save(any()) } returns getTestIngredient1()
        val ingredientCreateDTO = createUpdateTestIngredientDTO()
        Assertions.assertEquals(
            getTestIngredientDTO(), ingredientService.createUpdateIngredient(ingredientCreateDTO, 1)
        )
    }

    @Test
    fun `Test ingredient list`() {
        every { ingredientRepository.findAllByEventId(any()) } returns
                listOf(
                        Ingredient(1, "Testingredient", true, getEvent(), listOf(), listOf()),
                        Ingredient(2, "Testingredient2", true, getEvent(), listOf(), listOf())
                )

        Assertions.assertEquals(
            listOf(IngredientDTO(1, "Testingredient", true), IngredientDTO(2, "Testingredient2", true)),
            ingredientService.listIngredients(1)
        )
    }

    @Test
    fun `Test ingredient get`() {
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getTestIngredient1())
        Assertions.assertEquals(
            getTestIngredientDTO(), ingredientService.getIngredient(1,1)
        )
    }

    @Test
    fun `Test ingredient get not found`() {
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        val error = Assertions.assertThrows(
            ResourceNotFoundException::class.java
        ) { ingredientService.getIngredient(1, 1) }
        Assertions.assertEquals("Ingredient not found with ID: 1", error.message)
    }

    @Test
    fun `Test ingredient deletion`() {
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getTestIngredient1())
        every { ingredientRepository.delete(any()) } returns Unit
        Assertions.assertEquals(
            Unit, ingredientService.deleteIngredient(1,1)
        )
    }

    @Test
    fun `Test ingredient deletion fails`() {
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java
        ) { ingredientService.deleteIngredient(1, 1) }
        Assertions.assertEquals("Ingredient not found with ID: 1", error.message)

    }

    @Test
    fun `Test ingredient deletion not found`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        val error = Assertions.assertThrows(
            ResourceNotFoundException::class.java
        ) { ingredientService.getIngredient(1, 1) }
        Assertions.assertEquals("Ingredient not found with ID: 1", error.message)
    }

    @Test
    fun `Test ingredient update name`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { ingredientRepository.save(any()) } returns getTestIngredient1("TestingredientUpdated")
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getTestIngredient1())
        val ingredientCreateUpdateDTO = createUpdateTestIngredientDTO(1, "TestingredientUpdated")
        Assertions.assertEquals(
            IngredientDTO(
                1, "TestingredientUpdated", true
            ), ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 1)
        )
    }

    @Test
    fun `Test ingredient update mustBeProduced`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { ingredientRepository.save(any()) } returns getTestIngredient1( mustBeProduced = false)
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getTestIngredient1())
        val ingredientCreateUpdateDTO = createUpdateTestIngredientDTO(1, "TestingredientUpdated")
        Assertions.assertEquals(
            IngredientDTO(
                1, "TestIngredient", false
            ), ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 1)
        )
    }

    @Test
    fun `Test ingredient update not found`() {
        every { eventService.getEvent(any()) } returns getTestEventDTO()
        every { ingredientRepository.findByIdAndEventId(any(), any()) } returns empty()
        val ingredientCreateUpdateDTO = createUpdateTestIngredientDTO(2)

        val error = Assertions.assertThrows(
            ResourceNotFoundException::class.java
        ) { ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 1) }
        Assertions.assertEquals("Ingredient not found with ID: 2", error.message)
    }

    @Test
    fun `Test ingredient update not found event`() {
        every { eventService.getEvent(any()) } throws ResourceNotFoundException("Event not found with ID: 404")
        val ingredientCreateUpdateDTO = createUpdateTestIngredientDTO(2)

        val error = Assertions.assertThrows(
            ResourceNotFoundException::class.java
        ) { ingredientService.createUpdateIngredient(ingredientCreateUpdateDTO, 404) }
        Assertions.assertEquals("Event not found with ID: 404", error.message)
    }

    @Test
    fun `get ingredient by OrderIngredient ids`(){
        every { ingredientRepository.findByOrderIngredientsId(any()) } returns getTestIngredient1()
        Assertions.assertEquals(getTestIngredient1(), getTestIngredient1())
    }

    @Test
    fun `delete ingredient in use exception`() {
        every { ingredientRepository.findByIdAndEventId(1, any()) } returns Optional.of(getTestIngredient1(menuItems = listOf(getMenuItem())))
        val error = Assertions.assertThrows(
            ResourceInUseException::class.java
        ) { ingredientService.deleteIngredient(1, 1) }
        Assertions.assertEquals("Ingredient is used in menu items and cannot be deleted", error.message)
    }
}