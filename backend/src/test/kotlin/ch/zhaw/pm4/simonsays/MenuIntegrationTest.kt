package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.MenuItemMapper
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.MenuItem
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import ch.zhaw.pm4.simonsays.factory.MenuItemFactory
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
class MenuIntegrationTest : IntegrationTest() {
    private fun getMenuUrl(eventId: Long) = "/rest-api/v1/event/${eventId}/menu"


    @Autowired
    protected lateinit var menuItemMapper: MenuItemMapper

    @Autowired
    protected lateinit var menuItemFactory: MenuItemFactory

    private val tooLongMenuName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"
    private val arbitraryId = 9999999999

    private lateinit var testEvent: Event
    private lateinit var testMenuItem: MenuItem

    private val username = "admin"
    private val password = "mysecretpassword"

    @BeforeEach
    fun setUp() {
        testEvent = eventFactory.createEvent("Test Event Name", "TestEventPassword", 10)
        testMenuItem = menuItemFactory.createMenuItem(eventId = testEvent.id!!)
    }

    @Test
    @Transactional
    fun `Test menu creation should work with correct input`() {
        val menu = getCreateUpdateMenuDTO()
        mockMvc.put(getMenuUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menu)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name", CoreMatchers.equalTo(getCreateUpdateMenuDTO().name))
                }
            }
    }

    @Test
    @Transactional
    fun `Test menu creation should fail if menu name is missing`() {
        val menuItem = getCreateUpdateMenuDTO(null, null, null)
        val eventDto = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            mapOf("name" to "Menu name must be provided")
        )
        // when/then
        mockMvc.put(getMenuUrl(1)) {
            with(httpBasic(username, password))
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
    fun `Test menu creation should fail and display all errors`() {
        val menu = getCreateUpdateMenuDTO(null, tooLongMenuName, listOf(getMenuItemDTO()))
        val menuDTO = ErrorMessageModel(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            mapOf(
                "name" to "Menu name must be between 1 and 64 chars long",
            )
        )
        mockMvc.put(getMenuUrl(1)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menu)
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(menuDTO))
                }
            }
    }

    @Test
    @Transactional
    fun `Test retrieve all menus`() {
        menuFactory.createMenu(name = "test", eventId = testEvent.id!!, menuItems = listOf(testMenuItem))
        menuFactory.createMenu("name", eventId = testEvent.id!!, menuItems = listOf(testMenuItem))

        mockMvc.get(getMenuUrl(testEvent.id!!)){
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
    fun `Test retrieve menu`() {
        val menu = menuFactory.createMenu("Menu Test", testEvent.id!!, listOf(testMenuItem))
        val expectedJson =
            getMenuDTO(id = menu.id!!, menuItemDTOs = listOf(menuItemMapper.mapToMenuItemDTO(testMenuItem)))
        mockMvc.get("${getMenuUrl(testEvent.id!!)}/${menu.id}"){
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
    fun `Test menu not found`() {
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Menu not found with ID: $arbitraryId",
            null
        )

        mockMvc.get("${getMenuUrl(1)}/${arbitraryId}"){
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
    fun `Test update menu`() {
        val menu = menuFactory.createMenu(eventId = testEvent.id!!, menuItems = listOf(testMenuItem))
        val updateMenu =
            getCreateUpdateMenuDTO(menu.id, "integrationtest", listOf(menuItemMapper.mapToMenuItemDTO(testMenuItem)))
        val expectedReturn =
            getMenuDTO(menu.id!!, "integrationtest", listOf(menuItemMapper.mapToMenuItemDTO(testMenuItem)))

        mockMvc.put(getMenuUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenu)
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
    fun `Test menu update adding a menu item`() {
        val menu =
            menuFactory.createMenu(name = "testmenuitem", eventId = testEvent.id!!, menuItems = listOf(testMenuItem))
        val secondMenuItem = menuItemFactory.createMenuItem(eventId = testEvent.id!!)
        val updateMenu = getCreateUpdateMenuDTO(
            menu.id,
            "integrationtest",
            listOf(
                menuItemMapper.mapToMenuItemDTO(testMenuItem),
                menuItemMapper.mapToMenuItemDTO(secondMenuItem)
            )
        )
        val expectedReturn = getMenuDTO(
            menu.id!!,
            "integrationtest",
            listOf(
                menuItemMapper.mapToMenuItemDTO(testMenuItem),
                menuItemMapper.mapToMenuItemDTO(secondMenuItem)
            ),
            price = 2
        )

        mockMvc.put(getMenuUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenu)
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
    fun `test menu update removing an ingredient`() {
        val secondMenuItem = menuItemFactory.createMenuItem(eventId = testEvent.id!!)
        val menu = menuFactory.createMenu(
            "testmenuitem",
            testEvent.id!!,
            listOf(
                testMenuItem,
                secondMenuItem
            )
        )
        val updateMenu = getCreateUpdateMenuDTO(
            menu.id,
            "integrationtest",
            listOf(
                menuItemMapper.mapToMenuItemDTO(testMenuItem),
            )
        )
        val expectedReturn = getMenuDTO(
            menu.id!!,
            "integrationtest",
            listOf(
                menuItemMapper.mapToMenuItemDTO(testMenuItem),
            )
        )

        mockMvc.put(getMenuUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenu)
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
    fun `Test menu update should fail when invalid id provided`() {
        menuFactory.createMenu("testitem", eventId = testEvent.id!!)
        val updateMenu = getCreateUpdateMenuDTO(arbitraryId)
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Menu not found with ID: $arbitraryId",
            null
        )

        mockMvc.put(getMenuUrl(testEvent.id!!)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateMenu)
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
    fun `Test delete menu should fail when invalid id provided`() {
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Menu not found with ID: $arbitraryId",
            null
        )

        mockMvc.delete("${getMenuUrl(1)}/${arbitraryId}") {
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
    fun `Delete menu should succeed`() {
        val menu = menuFactory.createMenu(name = "testitem", eventId = testEvent.id!!, menuItems = listOf(testMenuItem))
        mockMvc.delete("${getMenuUrl(testEvent.id!!)}/${menu.id}") {
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
    fun `Test create menu should fail when invalid event id provided`() {
        val menu = getCreateUpdateMenuDTO()
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Event not found with ID: $arbitraryId",
            null
        )
        mockMvc.put(getMenuUrl(arbitraryId)) {
            with(httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(menu)
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