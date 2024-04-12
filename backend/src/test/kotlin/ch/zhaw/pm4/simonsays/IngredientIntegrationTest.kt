package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IngredientIntegrationTest : IntegrationTest() {

    private fun getIngredientsUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/ingredient"

    private fun getIngredientUrl(eventId: Long, ingredientId: Long) = "${getIngredientsUrl(eventId)}/${ingredientId}"

    private lateinit var globalEvent: Event

    @BeforeEach
    fun setup() {
        globalEvent = eventFactory.createEvent()
    }

    @Test
    @Transactional
    fun `should throw validation error no name`() {
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateIngredientDTO(null, null))
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    objectMapper.writeValueAsString(
                        ErrorMessageModel(
                            400,
                            "Validation failed",
                            mapOf("name" to "Ingredient name must be provided")
                        )
                    )
                }
            }
    }

    @Test
    @Transactional
    fun `should throw validation error too long name`() {
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateIngredientDTO(null, "a".repeat(65)))
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    objectMapper.writeValueAsString(
                        ErrorMessageModel(
                            400,
                            "Validation failed",
                            mapOf("name" to "Ingredient name must be between 3 and 64 chars long")
                        )
                    )
                }
            }
    }

    @Test
    @Transactional
    fun `should get ingredient not found`() {
        mockMvc.get(getIngredientUrl(globalEvent.id!!, 404))
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    objectMapper.writeValueAsString(ErrorMessageModel(404, "Ingredient not found with ID: 404", null))
                }
            }
    }

    @Test
    @Transactional
    @Order(1)
    fun `should add new ingredient`() {
        val ingredient = createUpdateIngredientDTO()
        val ingredientDTO = getIngredient1DTO()
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(ingredient)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ingredientDTO))
                }
            }
    }

    @Test
    @Transactional
    fun `should list ingredients`() {
        val ingredient = ingredientFactory.createIngredient(name = getIngredient1DTO().name)
        val ingredientDTOs = listOf(getIngredient1DTO(id = ingredient.id!!))
        // when/then
        mockMvc.get(getIngredientsUrl(ingredient.event.id!!))
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ingredientDTOs))
                }
            }
    }

    @Test
    @Transactional
    fun `should get ingredient`() {
        val ingredient = ingredientFactory.createIngredient(name = getIngredient1DTO().name)
        val ingredientDTO = getIngredient1DTO(id = ingredient.id!!)

        mockMvc.get(getIngredientUrl(ingredient.event.id!!, ingredient.id!!))
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ingredientDTO))
                }
            }
    }

    @Test
    @Transactional
    fun `should delete ingredient`() {
        val ingredient = ingredientFactory.createIngredient(name = getIngredient1DTO().name)
        mockMvc.delete(getIngredientUrl(ingredient.event.id!!, ingredient.id!!))
            .andDo { print() }
            .andExpect {
                status { isNoContent() }

            }
    }

}