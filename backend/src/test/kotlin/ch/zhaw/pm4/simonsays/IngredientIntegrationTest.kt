package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IngredientIntegrationTest : IntegrationTest() {

    private fun getIngredientsUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/ingredient"
    private fun getStationUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/station"

    private fun getIngredientUrl(eventId: Long, ingredientId: Long) = "${getIngredientsUrl(eventId)}/${ingredientId}"

    private lateinit var globalEvent: Event
    private val username = "admin"
    private val password = "mysecretpassword"

    @BeforeEach
    fun setup() {
        globalEvent = eventFactory.createEvent()
    }

    @Test
    @Transactional
    fun `should throw validation error no name`() {
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateTestIngredientDTO(null, null))
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
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateTestIngredientDTO(null, "a".repeat(65)))
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
        mockMvc.get(getIngredientUrl(globalEvent.id!!, 404)){
            with(httpBasic(username, password))
        }
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
    fun `should add new ingredient`() {
        val ingredient = createUpdateTestIngredientDTO()
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(ingredient)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name", CoreMatchers.equalTo(getTestIngredientDTO().name))
                }
            }
    }

    @Test
    @Transactional
    fun `should list ingredients`() {
        val ingredient = ingredientFactory.createIngredient(name = getTestIngredientDTO().name)
        val ingredientDTOs = listOf(getTestIngredientDTO(id = ingredient.id!!))
        // when/then
        mockMvc.get(getIngredientsUrl(ingredient.event.id!!)){
            with(httpBasic(username, password))
        }
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
        val ingredient = ingredientFactory.createIngredient(name = getTestIngredientDTO().name)
        val ingredientDTO = getTestIngredientDTO(id = ingredient.id!!)

        mockMvc.get(getIngredientUrl(ingredient.event.id!!, ingredient.id!!)){
            with(httpBasic(username, password))
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
    fun `should delete ingredient`() {
        val ingredient = ingredientFactory.createIngredient(name = getTestIngredientDTO().name)
        mockMvc.delete(getIngredientUrl(ingredient.event.id!!, ingredient.id!!)){
            with(httpBasic(username, password))
        }
            .andDo { print() }
            .andExpect {
                status { isNoContent() }

            }
    }

    @Test
    @Transactional
    fun `should update ingredient`() {
        val ingredient = ingredientFactory.createIngredient(name = getTestIngredientDTO().name, event = globalEvent)
        val ingredientDTO = createUpdateTestIngredientDTO(id = ingredient.id, mustBeProduced = false)
        mockMvc.put(getIngredientsUrl(globalEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(ingredientDTO)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name", CoreMatchers.equalTo(getTestIngredientDTO().name))
                    jsonPath("$.mustBeProduced", CoreMatchers.equalTo(false))
                }
            }
    }

    @Test
    fun `should remove ingredient`() {
        val event = eventFactory.createEvent(name = "Test-Event", password = "Test-password", numberOfTables = 12)
        val ingredient = ingredientFactory.createIngredient(name = "Test-Ingredient", event = event)
        val station = stationFactory.createStation(name = "Test-Station", ingredients = listOf(ingredient), eventId = event.id!!)

        // Validate that ingredient is linked with station
        mockMvc.get(getStationUrl(event.id!!) + "/" + station.id) {
            with(httpBasic(username, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.ingredients", IsCollectionWithSize.hasSize<Any>(1))
                        jsonPath("$.ingredients[0].name", CoreMatchers.equalTo("Test-Ingredient"))
                        jsonPath("$.ingredients[0].id", CoreMatchers.equalTo((ingredient.id!!).toInt()))
                    }
                }

        mockMvc.delete(getIngredientUrl(ingredient.event.id!!, ingredient.id!!)){
            with(httpBasic(username, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }

                }

        mockMvc.get(getIngredientUrl(ingredient.event.id!!, ingredient.id!!)){
            with(httpBasic(username, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }

                }

        mockMvc.delete(getStationUrl(event.id!!) + "/" + station.id){
            with(httpBasic(username, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }

                }
    }

}