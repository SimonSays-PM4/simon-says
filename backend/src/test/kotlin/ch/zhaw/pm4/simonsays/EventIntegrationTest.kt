package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

/*@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventIntegrationTest @Autowired constructor(
        private val mockMvc: MockMvc,
        private val objectMapper: ObjectMapper
){

    @Test
    fun `should add new event`() {
        val event = EventCreateDTO("integrationevent", "integrationeventpassword", 2)
        val eventDto = EventDTO("integrationevent", 2)
        // when/then
        mockMvc.post("/api/event") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(eventDto))
                }
            }
    }

}*/
