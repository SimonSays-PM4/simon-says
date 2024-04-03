package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IngredientIntegrationTest : IntegrationTest() {

    @Test
    fun `should throw validation error`() {
        // when/then
        mockMvc.put("/rest-api/v1/ingredient") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateIngredientDTO(null, null))
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    objectMapper.writeValueAsString(ErrorMessageModel(400, "Validation failed", mapOf("name" to "Ingredient name must be provided")))
                }
            }
    }

    @Test
    fun `should get ingredient not found`() {
        // when/then
        mockMvc.get("/rest-api/v1/ingredient/404")
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
    @Order(1)
    fun `should add new ingredient`() {
        val ingredient = createUpdateIngredientDTO()
        val ingredientDTO = getIngredient1DTO()
        // when/then
        mockMvc.put("/rest-api/v1/ingredient") {
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
    @Order(2)
    fun `should list ingredients`() {
        val ingredientDTOs = listOf(getIngredient1DTO())
        // when/then
        mockMvc.get("/rest-api/v1/ingredient")
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
    @Order(2)
    fun `should get ingredient`() {
        val ingredientDTO = getIngredient1DTO()
        // when/then
        mockMvc.get("/rest-api/v1/ingredient/1")
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
    @Order(3)
    fun `should delete ingredient`() {
        // when/then
        mockMvc.delete("/rest-api/v1/ingredient/1")
            .andDo { print() }
            .andExpect {
                status { isNoContent() }

            }
    }

}