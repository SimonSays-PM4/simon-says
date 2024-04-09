package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.EventCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemCreateUpdateDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import ch.zhaw.pm4.simonsays.factory.EventFactory
import ch.zhaw.pm4.simonsays.factory.MenuItemFactory
import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

/*@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MenuItemIntegrationTest : IntegrationTest() {

    @Autowired
    protected lateinit var menuItemFactory: MenuItemFactory

    @Autowired
    protected lateinit var eventFactory: EventFactory

    private val tooLongMenuItemName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"
    private val arbitraryId = 9999999999

    private lateinit var testEvent: Event

    @BeforeEach
    fun setUp() {
        testEvent = eventFactory.createEvent("Test Event Name", "TestEventPassword", 10)
    }

    @Test
    @Transactional
    fun `Test menu item creation should work with correct input`() {
        val menuItem = MenuItemCreateUpdateDTO(null, testEvent.id!!, "testmenuitemname")
        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menuItem)
        }
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$.name", equalTo("testmenuitemname"))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test menu item creation should fail if menu item name is missing`() {
        val menuItem = MenuItemCreateUpdateDTO(null, null, null)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf("name" to "Menu item name must be provided")
        )
        // when/then
        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menuItem)
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
        val event = EventCreateUpdateDTO(null,"integrationevent", "integrationeventpassword", -1)
        val eventDto = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf(
                        "numberOfTables" to "Number of tables must be greater or equal to 0"
                )
        )
        // when/then
        mockMvc.put("/rest-api/v1/event") {
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
    fun `Test menuitem creation should fail and display all errors`() {
        val menuItem = MenuItemCreateUpdateDTO(null, testEvent.id, tooLongMenuItemName)
        val menuItemDTO = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                mapOf(
                        "name" to "Menu item name must be between 1 and 64 chars long",
                )
        )
        // when/then
        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menuItem)
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(menuItemDTO))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test retrieve all events`() {
        menuItemFactory.createMenuItem("test")
        menuItemFactory.createMenuItem("name")

        mockMvc.get("/rest-api/v1/menuItem")
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        jsonPath("$", hasSize<Any>(2))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test retrieve menu item`() {
        val menuItem: MenuItem = menuItemFactory.createMenuItem("testmenuitem", testEvent.id!!)
        val expectedJson = MenuItemDTO(menuItem.id!!, testEvent.id!!, "testmenuitem")

        mockMvc.get("/rest-api/v1/menuItem/${menuItem.id}")
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(expectedJson))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test retrieving non existing menu item leads to not found`() {
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Menu item not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.get("/rest-api/v1/menuItem/${arbitraryId}")
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
    fun `Test update menu item`() {
        val menuItem: MenuItem = menuItemFactory.createMenuItem("testmenuitem")
        val updateMenuItem = MenuItemCreateUpdateDTO(menuItem.id,testEvent.id, "integrationtest")
        val expectedReturn = MenuItemDTO(menuItem.id!!, testEvent.id!!,"integrationtest")

        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenuItem)
        }
                .andDo { print() }
                .andExpect {
                    status { is2xxSuccessful() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(expectedReturn))
                    }
                }
    }

    @Test
    @Transactional
    fun `Test menu item update should fail when invalid id provided`() {
        menuItemFactory.createMenuItem("testitem")
        val updateMenuItem = MenuItemCreateUpdateDTO(arbitraryId,testEvent.id, "integrationtest")
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Menu item not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenuItem)
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
    fun `Test delete menu item should fail when invalid id provided`() {
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Menu item not found with ID: ${arbitraryId}",
                null
        )

        mockMvc.delete("/rest-api/v1/menuItem/${arbitraryId}") {
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
    fun `Test create menu item should fail when invalid event id provided`() {
        val menuItem = MenuItemCreateUpdateDTO(null, arbitraryId, "testmenuitemname")
        val expectedReturn = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                "Event not found with ID: ${arbitraryId}",
                null
        )
        mockMvc.put("/rest-api/v1/menuItem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menuItem)
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

}*/
