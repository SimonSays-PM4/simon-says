package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.types.StationCreateUpdateDTO
import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StationIntegrationTest : IntegrationTest() {

    private fun getStationUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/station"

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
        val station = getCreateUpdateStationDTO(null, null, false,null)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf("name" to "Station name must be provided")
        )
        // when/then
        mockMvc.put(getStationUrl(testEvent.id!!)) {
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
    fun `Test creating second assembly station leads to bad request`() {
        stationFactory.createStation(eventId = testEvent.id!!, assemblyStation = true)
        val createUpdateStation: StationCreateUpdateDTO = getCreateUpdateStationDTO(assemblyStation = true)
        val errorDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "An assembly station is already defined for this event",
                null
        )
        mockMvc.put(getStationUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createUpdateStation)
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(errorDto))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test station creation should fail and display all errors`() {
        val station = getCreateUpdateStationDTO(null, tooLongStationName, false, listOf(getTestIngredientDTO()))
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
        val station: Station = stationFactory.createStation(name = "Station test", eventId =  testEvent.id!!, ingredients = listOf(testIngredient))
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
        val updateStation = getCreateUpdateStationDTO(station.id, "integrationtest", false, listOf(ingredientMapper.mapToIngredientDTO(testIngredient)))
        val expectedReturn = getStationDTO(station.id!!, "integrationtest", false, listOf(ingredientMapper.mapToIngredientDTO(testIngredient)))

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
                false,
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                        ingredientMapper.mapToIngredientDTO(secondIngredient)
                )
        )
        val expectedReturn = getStationDTO(
                station.id!!,
                "teststation",
                false,
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
                name = "teststation",
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testIngredient,
                        secondIngredient
                )
        )
        val updateStation = getCreateUpdateStationDTO(
                station.id,
                "teststation",
                false,
                listOf(
                        ingredientMapper.mapToIngredientDTO(testIngredient),
                )
        )
        val expectedReturn = getStationDTO(
                station.id!!,
                "teststation",
                false,
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

    @Test
    @Transactional
    fun `Test creating an order should list the correct ingredients at the station`() {
        val testingredient1 = ingredientFactory.createIngredient("testingredient1", event = testEvent)
        val testingredient2 = ingredientFactory.createIngredient("testingredient2", event = testEvent)
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                ingredientId = testingredient1.id!!
        )
        val orderingredient2 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                ingredientId = testingredient2.id!!
        )
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val station = stationFactory.createStation(ingredients = listOf(testingredient1, testingredient2), eventId = testEvent.id!!)
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                    testingredient1,
                    testingredient2
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                        orderingredient2
                )
        )
        val expectedReturn = listOf(
                getOrderIngredientDTO(
                        id = orderingredient1.id!!,
                        name = orderingredient1.name,
                        state = orderingredient1.state
                ),
                getOrderIngredientDTO(
                        id = orderingredient2.id!!,
                        name = orderingredient2.name,
                        state = orderingredient2.state
                )
        )
        mockMvc.get(getStationUrl(testEvent.id!!) + "/${station.id}/view") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
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
    fun `Test creating an order with multiple involved stations shows correct view for station`() {
        val testingredient1 = ingredientFactory.createIngredient("testingredient1", event = testEvent)
        val testingredient2 = ingredientFactory.createIngredient("testingredient2", event = testEvent)
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                ingredientId = testingredient1.id!!
        )
        val orderingredient2 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                ingredientId = testingredient2.id!!
        )
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val station = stationFactory.createStation(ingredients = listOf(testingredient1), eventId = testEvent.id!!)
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                        testingredient2
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                        orderingredient2
                )
        )
        val expectedReturn = listOf(
                getOrderIngredientDTO(
                        id = orderingredient1.id!!,
                        name = orderingredient1.name,
                        state = orderingredient1.state
                )
        )
        mockMvc.get(getStationUrl(testEvent.id!!) + "/${station.id}/view") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
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
    fun `Marking Ingredients as complete at an assembly station works`() {
        val testingredient1 = ingredientFactory.createIngredient(name = "testingredient1", event = testEvent)
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                ingredientId = testingredient1.id!!
        )
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val station = stationFactory.createStation(ingredients = listOf(testingredient1), eventId = testEvent.id!!)
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                )
        )
        val expectedReturn = getOrderIngredientDTO(
                id = orderingredient1.id!!,
                name = orderingredient1.name,
                state = State.DONE
        )
        mockMvc.post(getStationUrl(testEvent.id!!) + "/${station.id}/view") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(getOrderIngredientDTO(
                    id = orderingredient1.id!!,
                    name = orderingredient1.name,
                    state = orderingredient1.state
            ))
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
    fun `Creating an assembly station works`() {
        val station = getCreateUpdateStationDTO(assemblyStation = true, ingredients = null)
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
                        jsonPath("$.name", CoreMatchers.equalTo(station.name))
                        jsonPath("$.assemblyStation", CoreMatchers.equalTo(station.assemblyStation))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test marking ingredient done for another station should fail`() {
        val testingredient1 = ingredientFactory.createIngredient("testingredient1")
        val testingredient2 = ingredientFactory.createIngredient("testingredient2")
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                name = testingredient1.name,
                ingredientId = testingredient1.id!!
        )
        val orderingredient2 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                name = testingredient2.name,
                ingredientId = testingredient2.id!!
        )
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val station1 = stationFactory.createStation(ingredients = listOf(testingredient1), eventId = testEvent.id!!)
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                        testingredient2
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                        orderingredient2
                )
        )
        val expectedReturn = ErrorMessageModel(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "This station is not allowed to update the state of the ingredient with the id: ${orderingredient2.id} (${orderingredient2.name})",
                errors = null
        )
        mockMvc.post(getStationUrl(testEvent.id!!) + "/${station1.id}/view") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(getOrderIngredientDTO(
                    id = orderingredient2.id!!,
                    name = orderingredient2.name,
                    state = orderingredient2.state
            ))
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(expectedReturn))
                    }
                }
    }

    @Test
    @Transactional
    fun `Display error when assembly view is accessed before assembly station exists`() {
        val expectedReturn = ErrorMessageModel(
                status = HttpStatus.NOT_FOUND.value(),
                message = "The event with id: ${testEvent.id} does not yet have an assembly station",
                errors = null
        )
        mockMvc.get(getStationUrl(testEvent.id!!) + "/assembly") {
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
    fun `Test assembly station should return nothing when nothing is ready`() {
        stationFactory.createStation(assemblyStation = true, eventId = testEvent.id!!)
        val order: FoodOrder = orderFactory.createOrder(eventId = testEvent.id!!)
        val testingredient1 = ingredientFactory.createIngredient("testingredient1")
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                name = testingredient1.name,
                ingredientId = testingredient1.id!!,
                state = State.IN_PROGRESS
        )
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                )
        )
        mockMvc.get(getStationUrl(testEvent.id!!) + "/assembly") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$", IsCollectionWithSize.hasSize<Any>(0))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test assembly Station displays the correct amount of outstanding orders`() {
        stationFactory.createStation(assemblyStation = true, eventId = testEvent.id!!)
        val order: FoodOrder = orderFactory.createOrder(eventId = testEvent.id!!)
        val testingredient1 = ingredientFactory.createIngredient("testingredient1")
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                name = testingredient1.name,
                ingredientId = testingredient1.id!!,
                state = State.DONE
        )
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                )
        )
        mockMvc.get(getStationUrl(testEvent.id!!) + "/assembly") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$", IsCollectionWithSize.hasSize<Any>(1))
                }
            }
    }

    /*@Test
    @Transactional
    fun `Test assembly station displays the correct information`() {
        stationFactory.createStation(assemblyStation = true, eventId = testEvent.id!!)
        val order: FoodOrder = orderFactory.createOrder(eventId = testEvent.id!!)
        val testingredient1 = ingredientFactory.createIngredient("testingredient1")
        val orderingredient1 = orderIngredientFactory.createOrderIngredient(
                eventId = testEvent.id!!,
                name = testingredient1.name,
                ingredientId = testingredient1.id!!,
                state = State.DONE
        )
        val menuItem = menuItemFactory.createMenuItem(
                eventId = testEvent.id!!,
                ingredients = listOf(
                        testingredient1,
                )
        )
        orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = menuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                        orderingredient1,
                )
        )

        mockMvc.get(getStationUrl(testEvent.id!!) + "/assembly") {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(order))
                    }
                }
    }*/
}