package ch.zhaw.pm4.simonsays

import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class HealthIntegrationTest : IntegrationTest() {


    @Test
    @Transactional
    fun `Test health endpoint`() {
        // get endpoint without authentication
        mockMvc.get("/rest-api/v1/health")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.state", CoreMatchers.equalTo("up"))
                }
            }
    }

}