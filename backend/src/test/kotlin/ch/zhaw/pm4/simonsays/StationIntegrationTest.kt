package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.entity.Station
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import ch.zhaw.pm4.simonsays.factory.StationFactory
import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StationIntegrationTest : IntegrationTest() {

    private fun getStationUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/station"

    @Autowired
    protected lateinit var stationFactory: StationFactory

    @Autowired
    protected lateinit var ingredientMapper: IngredientMapper

    private val tooLongStationName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"
    private val arbitraryId = 9999999999

    private val username = "admin"
    private val password = "mysecretpassword"

    private lateinit var testEvent: Event
    private lateinit var testIngredient: Ingredient

    @BeforeEach
    fun setUp() {
        testEvent = eventFactory.createEvent("Test Event Name", "TestEventPassword", 10)
        testIngredient = ingredientFactory.createIngredient("Default Ingredient Name")
    }

    @Test
    @Transactional
    fun `Test station creation should work with correct input`() {
        val station = getCreateUpdateStationDTO()
        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(station)
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.name", CoreMatchers.equalTo(getCreateUpdateStationDTO().name))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test station creation should fail if station name is missing`() {
        val station = getCreateUpdateStationDTO(null, null, null)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf("name" to "Station name must be provided")
        )
        // when/then
        mockMvc.put(getStationUrl(1)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(station)
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
    fun `Test station creation should fail and display all errors`() {
        val station = getCreateUpdateStationDTO(null, tooLongStationName, listOf(getTestIngredientDTO()))
        val stationDTO = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf(
                        "name" to "Station name must be between 1 and 64 chars long",
                )
        )
        // when/then
        mockMvc.put(getStationUrl(1)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(station)
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(stationDTO))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test retrieve all stations`() {
        stationFactory.createStation(name = "test", eventId = testEvent.id!!, ingredients = listOf(testIngredient))
        stationFactory.createStation("name", eventId = testEvent.id!!, ingredients = listOf(testIngredient))

        mockMvc.get(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$", IsCollectionWithSize.hasSize<Any>(2))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test retrieve station`() {
        val station: Station = stationFactory.createStation("Station test", testEvent.id!!, listOf(testIngredient))
        val expectedJson = getStationDTO(id = station.id!!, ingredientDTOs = listOf(getTestIngredientDTO(id = testIngredient.id, name = testIngredient.name)))

        mockMvc.get("${getStationUrl(testEvent.id!!)}/${station.id}") {
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
    fun `Test retrieving non existing station leads to not found`() {
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Station not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.get("${getStationUrl(1)}/${arbitraryId}") {
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
    fun `Test update station`() {
        val station: Station = stationFactory.createStation(eventId = testEvent.id!!, ingredients = listOf(testIngredient))
        val updateStation = getCreateUpdateStationDTO(station.id, "integrationtest", listOf(ingredientMapper.mapToIngredientDTO(testIngredient)))
        val expectedReturn = getStationDTO(station.id!!, "integrationtest", listOf(ingredientMapper.mapToIngredientDTO(testIngredient)))

        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStation)
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
    fun `Test station update adding an ingredient`() {
        val station: Station = stationFactory.createStation(name = "teststation", eventId = testEvent.id!!, ingredients = listOf(testIngredient))
        val secondIngredient: Ingredient = ingredientFactory.createIngredient()
        val updateStation = getCreateUpdateStationDTO(
                station.id,
                "teststation",
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                        ingredientMapper.mapToIngredientDTO(secondIngredient)
                )
        )
        val expectedReturn = getStationDTO(
                station.id!!,
                "teststation",
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                        ingredientMapper.mapToIngredientDTO(secondIngredient)
                )
        )

        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStation)
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
    fun `test station update removing an ingredient`() {
        val secondIngredient: Ingredient = ingredientFactory.createIngredient()
        val station: Station = stationFactory.createStation(
                "teststation",
                testEvent.id!!,
                listOf(
                        testIngredient,
                        secondIngredient
                )
        )
        val updateStation = getCreateUpdateStationDTO(
                station.id,
                "teststation",
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                )
        )
        val expectedReturn = getStationDTO(
                station.id!!,
                "teststation",
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                )
        )

        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStation)
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
    fun `Test station update should fail when invalid id provided`() {
        stationFactory.createStation(name = "teststation", eventId = testEvent.id!!)
        val updateStation = getCreateUpdateStationDTO(arbitraryId)
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Station not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStation)
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
    fun `Test delete station should fail when invalid id provided`() {
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Station not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.delete("${getStationUrl(testEvent.id!!)}/${arbitraryId}") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
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
    fun `Delete station should succeed`() {
        val station: Station = stationFactory.createStation(name = "teststation", eventId = testEvent.id!!, ingredients = listOf(testIngredient))
        mockMvc.delete("${getStationUrl(testEvent.id!!)}/${station.id}") {
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
    fun `Test create station should fail when invalid event id provided`() {
        val station = getCreateUpdateStationDTO()
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Event not found with ID: ${arbitraryId}",
                null
        )
        mockMvc.put(getStationUrl(arbitraryId)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(station)
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