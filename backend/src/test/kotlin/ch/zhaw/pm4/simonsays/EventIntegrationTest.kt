package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.EventDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EventIntegrationTest : IntegrationTest() {

    private val tooLongEventName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"
    private val arbitraryId = 9999999999

    private val username = "admin"
    private val password = "mysecretpassword"

    @Test
    @Transactional
    @Order(1)
    fun `Test event creation should should work with correct input`() {
        val event = EventCreateUpdateDTO(null, "eventusedfortesting", "eventusedfortesting", 2)
        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(event)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name", CoreMatchers.equalTo("eventusedfortesting"))
                    jsonPath("$.password", CoreMatchers.equalTo("eventusedfortesting"))
                    jsonPath("$.numberOfTables", CoreMatchers.equalTo(2))
                }
            }
    }

    @Test
    @Transactional
    fun `Test event creation should fail if event name is missing`() {
        val event = EventCreateUpdateDTO(null, "5", "integrationeventpassword", 2)
        val eventDto = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            mapOf("name" to "Event name must be between 5 and 64 chars long")
        )
        // when/then
        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
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
    fun `Test event creation should fail if number of tables is negative`() {
        val event = EventCreateUpdateDTO(null, "integrationevent", "integrationeventpassword", -1)
        val eventDto = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            mapOf(
                "numberOfTables" to "Number of tables must be greater or equal to 0"
            )
        )
        // when/then
        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
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
    fun `Test event creation should fail and display all errors`() {
        val event = EventCreateUpdateDTO(null, tooLongEventName, null, 2)
        val eventDto = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            mapOf(
                "name" to "Event name must be between 5 and 64 chars long",
                "password" to "Event password must be provided"
            )
        )
        // when/then
        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
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
    fun `Test retrieve all events`() {
        eventFactory.createEvent("test", "test", 0)
        eventFactory.createEvent("name", "test", 2)

        mockMvc.get("/rest-api/v1/event") {
            with(httpBasic(username, password))
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$", hasSize<Any>(2))
                }
            }
    }

    @Test
    @Transactional
    fun `Test retrieve event`() {
        val event: Event = eventFactory.createEvent("test", "test", 0)

        // Since the response is expected to be an array, wrap the expected DTO in a list
        val expectedJson = EventDTO(id = event.id, name = "test", password = "test", numberOfTables = 0)

        mockMvc.get("/rest-api/v1/event/${event.id}") {
            with(httpBasic(username, password))
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(expectedJson))
                }
            }
    }

    @Test
    @Transactional
    fun `Test retrieving non existing event leads to not found`() {
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Event not found with ID: ${arbitraryId}",
            null
        )

        mockMvc.get("/rest-api/v1/event/${arbitraryId}"){
            with(httpBasic(username, password))
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(expectedReturn))
                }
            }
    }

    @Test
    @Transactional
    fun `Test update event`() {
        val event: Event = eventFactory.createEvent("test", "test", 0)
        val updateEvent = EventCreateUpdateDTO(event.id,"integrationtest", "testtest", 3)
        val expectedReturn = EventDTO(id = event.id, name = "integrationtest", password = "testtest", numberOfTables = 3)

        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateEvent)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(expectedReturn))
                }
            }
    }

    @Test
    @Transactional
    fun `Test delete event`() {
        val event: Event = eventFactory.createEvent("test", "test", 0)
        mockMvc.delete("/rest-api/v1/event/${event.id}") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Transactional
    fun `Test event update should fail when invalid id provided`() {
        eventFactory.createEvent("test", "test", 0)
        val updateEvent = EventCreateUpdateDTO(arbitraryId, "integrationtest", "testtest", 3)
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Event not found with ID: ${arbitraryId}",
            null
        )

        mockMvc.put("/rest-api/v1/event") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateEvent)
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(expectedReturn))
                }
            }
    }

}
