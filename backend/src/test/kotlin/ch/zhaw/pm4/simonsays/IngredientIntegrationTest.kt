package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientCreateDTO
import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IngredientIntegrationTest : IntegrationTest() {

    @Test
    fun `should throw validation error`() {
        //val ingredient = IngredientCreateDTO()
        // when/then
        mockMvc.post("/rest-api/v1/ingredient") {
            contentType = MediaType.APPLICATION_JSON
            content = "{\"name\":null}"
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString("Ingredient name must be provided"))
                }
            }
    }
    @Test
    @Order(1)
    fun `should add new ingredient`() {
        val ingredient = IngredientCreateDTO("integrationingredient")
        val ingredientDTO = IngredientDTO(1,"integrationingredient")
        // when/then
        mockMvc.post("/rest-api/v1/ingredient") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(ingredient)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ingredientDTO))
                }
            }
    }
    @Test
    @Order(2)
    fun `should list ingredients`() {
        val ingredientDTOs = listOf(IngredientDTO(1,"integrationingredient"))
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
        val ingredientDTO = IngredientDTO(1,"integrationingredient")
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