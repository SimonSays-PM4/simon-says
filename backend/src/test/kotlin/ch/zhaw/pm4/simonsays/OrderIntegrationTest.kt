package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.api.types.MenuDTO
import ch.zhaw.pm4.simonsays.api.types.MenuItemDTO
import ch.zhaw.pm4.simonsays.entity.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OrderIntegrationTest : IntegrationTest() {

    private fun getOrderUrl(eventId: Long) = "/rest-api/v1/event/$eventId/order"

    private val arbitraryId = 9999999999

    private val username = "admin"
    private val password = "mysecretpassword"

    private lateinit var testEvent: Event
    private lateinit var testIngredient: Ingredient
    private lateinit var testMenuItem: MenuItem
    private lateinit var testMenu: Menu
    private lateinit var testStation: Station

    @BeforeEach
    fun setUp() {
        testEvent = eventFactory.createEvent("Test Event Name", "TestEventPassword", 10)
        testIngredient = ingredientFactory.createIngredient("Order Ingredient Test", event = testEvent)
        testStation = stationFactory.createStation(name = "Test Station", assemblyStation = false, eventId = testEvent.id!!, ingredients = listOf(testIngredient))
        testMenuItem = menuItemFactory.createMenuItem("Test Menu Item Name", eventId = testEvent.id!!)
        testMenu = menuFactory.createMenu("Test Menu Name", eventId = testEvent.id!!)
    }

    @Test
    @Transactional
    @Order(1)
    fun `create order should succeed`() {
        val testIngredient2 = ingredientFactory.createIngredient("Order Ingredient Test", event = testEvent, mustBeProduced = false)
        val orderCreateDTO = getOrderCreateDTO(
            menus = mutableListOf(
                getMenuDTO(
                    id = testMenu.id!!,
                    menuItemDTOs = mutableListOf(
                        getMenuItemDTO(
                            id = testMenuItem.id!!,
                            ingredientDTOs = mutableListOf(getTestIngredientDTO(id = testIngredient.id!!), getTestIngredientDTO(id = testIngredient2.id!!))
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
                        objectMapper.writeValueAsString(getOrderDTOCreated1())
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
                        objectMapper.writeValueAsString(
                            getNotFoundError("Menu", arbitraryId)
                        )
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
                        objectMapper.writeValueAsString(getOrderDTOCreated2())
                    )
                }
            }
    }

    @Test
    @Transactional
    @Order(4)
    fun `test huge order should succeed`() {
        val menus = mutableListOf<MenuDTO>()
        val menuItems = mutableListOf<MenuItemDTO>()
        val ingredients = mutableListOf<IngredientDTO>()
        for (i in 1..10) {
            getTestIngredientDTO(id = testIngredient.id!!).let { ingredients.add(it) }
        }
        for (i in 1..10) {
            getMenuItemDTO(
                id = testMenuItem.id!!,
                ingredientDTOs = ingredients
            ).let { menuItems.add(it) }
        }
        for (i in 1..10) {
            getMenuDTO(
                id = testMenu.id!!,
                menuItemDTOs = menuItems
            ).let { menus.add(it) }
        }

        val orderCreateDTO = getOrderCreateDTO(
            menus = menus,
            menuItems = menuItems
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
                    jsonPath("$.menus.size()") { value(10) }
                    jsonPath("$.menuItems.size()") { value(10) }
                    jsonPath("$.totalPrice") { value(20.0) }
                    jsonPath("$.menus[0].menuItems.size()") { value(10) }
                    jsonPath("$.menus[0].menuItems[0].ingredients.size()") { value(10) }
                    jsonPath("$.state") { value(State.IN_PROGRESS.name) }
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

        mockMvc.delete("${getOrderUrl(testEvent.id!!)}/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("Order", arbitraryId)))
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
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("Order", order.id!!)))
                }
            }

    }

    @Test
    @Transactional
    fun `update order ingredient state should fail invalid event id`() {
        val ingredientOrder =
            orderIngredientFactory.createOrderIngredient(eventId = testEvent.id!!, ingredientId = testIngredient.id!!)

        mockMvc.put("${getOrderUrl(arbitraryId)}/ingredient/${ingredientOrder.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderIngredient", ingredientOrder.id!!)))
                }
            }
    }

    @Test
    @Transactional
    fun `Update order Menu Item State should succeed when correct values provided`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val menuItemOrder =
            orderMenuItemFactory.createOrderMenuItem(
                eventId = testEvent.id!!,
                menuItemId = testMenuItem.id!!,
                order = order,
                orderIngredients = mutableListOf(
                    orderIngredientFactory.createOrderIngredient(
                        eventId = testEvent.id!!,
                        ingredientId = testIngredient.id!!
                    )
                )
            )

        mockMvc.put("${getOrderUrl(arbitraryId)}/menuitem/${menuItemOrder.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderMenuItem", menuItemOrder.id!!)))
                }
            }
    }

    @Test
    @Transactional
    fun `update order menu state should fail invalid event id`() {
        val order = orderFactory.createOrder(eventId = testEvent.id!!)
        val menuOrder = orderMenuFactory.createOrderMenu(
            eventId = testEvent.id!!,
            menuId = testMenu.id!!,
            order = order,
            orderMenuItems = mutableListOf(
                orderMenuItemFactory.createOrderMenuItem(
                    eventId = testEvent.id!!,
                    menuItemId = testMenuItem.id!!,
                    order = order,
                    orderIngredients = mutableListOf(
                        orderIngredientFactory.createOrderIngredient(
                            eventId = testEvent.id!!,
                            ingredientId = testIngredient.id!!
                        )
                    )
                )
            )
        )

        mockMvc.put("${getOrderUrl(arbitraryId)}/menu/${menuOrder.id!!}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderMenu", menuOrder.id!!)))
                }
            }
    }

    @Test
    @Transactional
    fun `update order ingredient state should fail invalid order ingredient id`() {
        mockMvc.put("${getOrderUrl(testEvent.id!!)}/ingredient/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderIngredient", arbitraryId)))
                }
            }
    }

    @Test
    @Transactional
    fun `update order menu item state should fail invalid order menu item id`() {
        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menuitem/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderMenuItem", arbitraryId)))
                }
            }
    }

    @Test
    @Transactional
    fun `update order menu state should fail invalid order menu id`() {
        mockMvc.put("${getOrderUrl(testEvent.id!!)}/menu/${arbitraryId}") {
            with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
            contentType = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(getNotFoundError("OrderMenu", arbitraryId)))
                }
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
        val expectedReturn = getOrderMenuItemDTO(
            state = State.DONE, id = orderMenuItem.id!!,
            ingredients = mutableListOf(getOrderIngredientDTO(id = orderIngredient.id!!))
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
                    json(objectMapper.writeValueAsString(expectedReturn))
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
        val expectedReturn = getOrderIngredientDTO(
            state = State.DONE, id = orderIngredient.id!!
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
                    json(objectMapper.writeValueAsString(expectedReturn))
                }
            }
    }

}