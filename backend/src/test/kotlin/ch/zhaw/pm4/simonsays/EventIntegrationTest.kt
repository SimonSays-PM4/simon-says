package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventCreateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import ch.zhaw.pm4.simonsays.factory.EventFactory
import jakarta.transaction.Transactional
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.shaded.org.bouncycastle.asn1.cms.CMSAttributes.contentType

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventIntegrationTest : IntegrationTest() {

    @Autowired
    protected lateinit var eventFactory: EventFactory

    private val tooLongEventName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"

    @Test
    @Transactional
    fun `event creation should should work with correct input`() {
        val event = EventCreateDTO("integrationevent", "integrationeventpassword", 2)
        val eventDto = EventDTO("integrationevent", 2, 1)
        // when/then
        mockMvc.post("/rest-api/v1/event") {
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

    @Test
    @Transactional
    fun `event creation should fail if event name is missing`() {
        val event = EventCreateDTO("5", "integrationeventpassword", 2)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf("name" to "Event name must be between 5 and 64 chars long")
        )
        // when/then
        mockMvc.post("/rest-api/v1/event") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }
        .andDo { print() }
        .andExpect {
            status { isBadRequest() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(objectMapper.writeValueAsString(eventDto))
            }
        }
    }

    @Test
    @Transactional
    fun `event creation should fail if number of tables is negative`() {
        val event = EventCreateDTO("integrationevent", "integrationeventpassword", -1)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf(
                        "numberOfTables" to "Number of tables must be greater or equal to 0"
                )
        )
        // when/then
        mockMvc.post("/rest-api/v1/event") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(eventDto))
                    }
                }
    }

    @Test
    @Transactional
    fun `event creation should fail and display all errors`() {
        val event = EventCreateDTO(tooLongEventName, null, 2)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf(
                        "name" to "Event name must be between 5 and 64 chars long",
                        "password" to "Event password must be provided"
                )
        )
        // when/then
        mockMvc.post("/rest-api/v1/event") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(eventDto))
                }
            }
    }

    @Test
    @Transactional
    fun `retrieve all events`() {
        eventFactory.createEvent("test", "test", 0)
        eventFactory.createEvent("name", "test", 2)

        val expectedResponse = listOf(
                EventDTO("name", 2, 2),
                EventDTO("test", 0, 1)
        )
        val expectedJson = objectMapper.writeValueAsString(expectedResponse)

        mockMvc.get("/rest-api/v1/event")
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$", hasSize<Any>(2))
                    }
                }
    }

    /*@Test
    @Transactional
    fun `retrieve event`() {
        eventFactory.createEvent("test", "test", 0)

        // Since the response is expected to be an array, wrap the expected DTO in a list
        val expectedJson = objectMapper.writeValueAsString(listOf(EventDTO("test", 0, 1)))

        mockMvc.get("/rest-api/v1/event")
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(expectedJson)
                    }
                }
    }*/


}
