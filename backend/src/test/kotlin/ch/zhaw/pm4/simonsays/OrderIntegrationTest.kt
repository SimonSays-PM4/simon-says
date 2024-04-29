package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.*
import ch.zhaw.pm4.simonsays.exception.ErrorMessageModel
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OrderIntegrationTest : IntegrationTest() {

    private fun getOrderUrl(eventId: Long) = "/rest-api/v1/event/$eventId/order"

    private val tooLongStationName: String = "hafdnvgnumnluizouvsathtjeyqpnelscybzbgpkyizsdtxnhjfyfomhdlbouwwqz"
    private val arbitraryId = 9999999999

    private val username = "admin"
    private val password = "mysecretpassword"

    private lateinit var testEvent: Event
    private lateinit var testIngredient: Ingredient
    private lateinit var testMenuItem: MenuItem
    private lateinit var testMenu: Menu

    @BeforeEach
    fun setUp() {
        testEvent = eventFactory.createEvent("Test Event Name", "TestEventPassword", 10)
        testIngredient = ingredientFactory.createIngredient("Order Ingredient Test", event = testEvent)
        testMenuItem = menuItemFactory.createMenuItem("Test Menu Item Name", eventId = testEvent.id!!)
        testMenu = menuFactory.createMenu("Test Menu Name", eventId = testEvent.id!!)
    }

    @Test
    @Transactional
    @Order(1)
    fun `create order should succeed`() {
        val orderCreateDTO = getOrderCreateDTO(
            menus = mutableListOf(
                getMenuDTO(
                    id = testMenu.id!!,
                    menuItemDTOs = mutableListOf(
                        getMenuItemDTO(
                            id = testMenuItem.id!!,
                            ingredientDTOs = mutableListOf(getTestIngredientDTO(id = testIngredient.id!!))
                        )
                    )
                )
            ),
            menuItems = mutableListOf(
                getMenuItemDTO(
                    id = testMenuItem.id!!,
                    ingredientDTOs = mutableListOf(getTestIngredientDTO(id = testIngredient.id!!))
                )
            )
        )
        val expectedReturn = getOrderDTO(
            menus = mutableListOf(
                getOrderMenuDTO(
                    name = "Test Menu Name",
                    price = 1.0,
                    menuItems = mutableListOf(
                        getOrderMenuItemDTO(
                            id = 2,
                            name = "Test Menu Item Name",
                            price = 1.0,
                            ingredients = mutableListOf(
                                getOrderIngredientDTO(id = 2)
                            )
                        )
                    )
                )
            ),
            menuItems = mutableListOf(
                getOrderMenuItemDTO(price = 1.0, name = "Test Menu Item Name")
            ),
            totalPrice = 2.0,
        )

        mockMvc.put(getOrderUrl(testEvent.id!!)) {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(orderCreateDTO)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        objectMapper.writeValueAsString(expectedReturn)
                    )
                }
            }
    }

    @Test
    @Transactional
    @Order(2)
    fun `create order should fail menu not found`() {
        val orderCreateDTO = getOrderCreateDTO(
            menus = mutableListOf(
                getMenuDTO(
                    id = arbitraryId,
                )
            ),
        )
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Menu not found with ID: ${arbitraryId}",
            null
        )

        mockMvc.put(getOrderUrl(testEvent.id!!)) {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(orderCreateDTO)
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        objectMapper.writeValueAsString(expectedReturn)
                    )

                }
            }
    }

    @Test
    @Transactional
    @Order(3)
    fun `create takeAway order should succeed`() {
        val orderCreateDTO = getOrderCreateDTO(
            menus = mutableListOf(
                getMenuDTO(
                    id = testMenu.id!!,
                    menuItemDTOs = mutableListOf(
                        getMenuItemDTO(
                            id = testMenuItem.id!!,
                            ingredientDTOs = mutableListOf(getTestIngredientDTO(id = testIngredient.id!!))
                        )
                    )
                )
            ),
            menuItems = mutableListOf(
                getMenuItemDTO(
                    id = testMenuItem.id!!,
                    ingredientDTOs = mutableListOf(getTestIngredientDTO(id = testIngredient.id!!))
                )
            ),
            tableNumber = null,
            isTakeAway = true
        )

        val expectedReturn = getOrderDTO(
            id = 2,
            menus = mutableListOf(
                getOrderMenuDTO(
                    id = 2,
                    name = "Test Menu Name",
                    price = 1.0,
                    menuItems = mutableListOf(
                        getOrderMenuItemDTO(
                            id = 4,
                            name = "Test Menu Item Name",
                            price = 1.0,
                            ingredients = mutableListOf(
                                getOrderIngredientDTO(id = 4)
                            )
                        )
                    )
                )
            ),
            menuItems = mutableListOf(
                getOrderMenuItemDTO(
                    id = 3,
                    price = 1.0,
                    name = "Test Menu Item Name",
                    ingredients = mutableListOf(getOrderIngredientDTO(id = 3))
                )
            ),
            totalPrice = 2.0,
            tableNumber = null,
            isTakeAway = true
        )

        mockMvc.put(getOrderUrl(testEvent.id!!)) {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(orderCreateDTO)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        objectMapper.writeValueAsString(expectedReturn)
                    )
                }
            }
    }

    @Test
    @Transactional
    fun `test list orders`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val order2 = orderFactory.createOrder(eventId = testEvent.id!!)
        val order3 = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.get(getOrderUrl(testEvent.id!!)) {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(3) }
                jsonPath("$[0].id") { value(order.id!!) }
                jsonPath("$[1].id") { value(order2.id!!) }
                jsonPath("$[2].id") { value(order3.id!!) }
            }
    }

    @Test
    @Transactional
    fun `delete order should succeed`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.delete("${getOrderUrl(testEvent.id!!)}/${order.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

    }

    @Test
    @Transactional
    fun `delete order should fail invalid id`() {
        val expectedReturn = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            "Order not found with ID: ${arbitraryId}",
            null
        )

        mockMvc.delete("${getOrderUrl(testEvent.id!!)}/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
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
    fun `delete order should fail invalid event id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.delete("${getOrderUrl(arbitraryId)}/${order.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }

    }

    @Test
    @Transactional
    fun `update order ingredient state should fail invalid event id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(arbitraryId)}/ingredient/${order.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order menu item state should fail invalid event id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(arbitraryId)}/menuitem/${order.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order menu state should fail invalid event id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(arbitraryId)}/menu/${order.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order ingredient state should fail invalid order ingredient id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(testEvent.id!!)}/ingredient/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order menu item state should fail invalid order menu item id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menuitem/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order menu state should fail invalid order menu id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)

        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menu/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Transactional
    fun `update order menu state should succeed`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val orderIngredient = orderIngredientFactory.createOrderIngredient(
            name = "Order Ingredient Test",
            eventId = testEvent.id!!,
            ingredientId = testIngredient.id!!
        )
        val orderMenuItem = orderMenuItemFactory.createOrderMenuItem(
            eventId = testEvent.id!!,
            menuItemId = testMenuItem.id!!,
            price = 15.20,
            order = order,
            orderIngredients = mutableListOf(orderIngredient)
        )
        val orderMenu = orderMenuFactory.createOrderMenu(
            eventId = testEvent.id!!,
            order = order,
            menuId = testMenu.id!!,
            price = 15.20,
            orderMenuItems = mutableListOf(orderMenuItem)
        )
        val expectedReturn = getOrderMenuDTO(
            state = State.DONE, id = orderMenu.id!!,
            menuItems = mutableListOf(
                getOrderMenuItemDTO(
                    id = orderMenuItem.id!!,
                    ingredients = mutableListOf(getOrderIngredientDTO(id = orderIngredient.id!!))
                )
            )
        )
        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menu/${orderMenu.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
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
    fun `update order menu item state should succeed`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val orderIngredient = orderIngredientFactory.createOrderIngredient(
            name = "Order Ingredient Test",
            eventId = testEvent.id!!,
            ingredientId = testIngredient.id!!
        )
        val orderMenuItem = orderMenuItemFactory.createOrderMenuItem(
            eventId = testEvent.id!!,
            menuItemId = testMenuItem.id!!,
            price = 15.20,
            order = order,
            orderIngredients = mutableListOf(orderIngredient)
        )
        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menuitem/${orderMenuItem.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        objectMapper.writeValueAsString(
                            getOrderMenuItemDTO(
                                id = orderMenuItem.id!!,
                                state = State.DONE,
                                ingredients = mutableListOf(getOrderIngredientDTO(id = orderIngredient.id!!))
                            )
                        )
                    )
                }
            }
    }

    @Test
    @Transactional
    fun `update order ingredient state should succeed`() {
        val orderIngredient = orderIngredientFactory.createOrderIngredient(
            name = "Order Ingredient Test",
            eventId = testEvent.id!!,
            ingredientId = testIngredient.id!!
        )

        mockMvc.put("${getOrderUrl(testEvent.id!!)}/ingredient/${orderIngredient.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getOrderIngredientDTO(id = orderIngredient.id!!, state = State.DONE)))
                }
            }
    }

}