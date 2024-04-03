package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapperImpl
import ch.zhaw.pm4.simonsays.api.types.IngredientCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
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

    private lateinit var ingredientService: IngredientService

    @BeforeEach
    fun setup() {
        ingredientRepository = mockk(relaxed = true)
        ingredientService = IngredientServiceImpl(ingredientRepository, IngredientMapperImpl())
    }

    @Test
    fun `Test ingredient creation`() {
        every { ingredientRepository.save(any()) } returns Ingredient(
            "Testingredient", 1
        )
        val ingredientCreateDTO = IngredientCreateUpdateDTO(
            null,
            "Testingredient"
        )
        Assertions.assertEquals(
            IngredientDTO(
            1, "Testingredient"
        ), ingredientService.createUpdateIngredient(ingredientCreateDTO))
    }
    @Test
    fun `Test ingredient list`() {
        every { ingredientRepository.findAll() } returns
            listOf(Ingredient("Testingredient", 1), Ingredient("Testingredient2", 2))

        Assertions.assertEquals(
            listOf(IngredientDTO(1, "Testingredient"), IngredientDTO(2, "Testingredient2")), ingredientService.listIngredients())
    }
    @Test
    fun `Test ingredient get`() {
        every { ingredientRepository.findById(1) } returns Optional.of(Ingredient(
            "Testingredient", 1
        ))
        Assertions.assertEquals(
            IngredientDTO(
                1, "Testingredient"
            ), ingredientService.getIngredient(1))
    }

    @Test
    fun `Test ingredient get not found`() {
        every { ingredientRepository.findById(any()) } returns empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.getIngredient(1) },
            "Ingredient not found with ID: 1"
        )
    }


    @Test
    fun `Test ingredient deletion`() {
        every { ingredientRepository.delete(any()) } returns Unit
        Assertions.assertEquals(
           Unit, ingredientService.deleteIngredient(1))
    }

    @Test
    fun `Test ingredient deletion not found`() {
        every { ingredientRepository.findById(any()) } returns empty()
        Assertions.assertThrows(
            ResourceNotFoundException::class.java,
            { ingredientService.getIngredient(1) },
            "Ingredient not found with ID: 1"
        )
    }
}