package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.factory.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class IntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

   @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var ingredientFactory: IngredientFactory

    @Autowired
    protected lateinit var menuItemFactory: MenuItemFactory

    @Autowired
    protected lateinit var stationFactory: StationFactory

    @Autowired
    protected lateinit var menuFactory: MenuFactory

    @Autowired
    protected lateinit var orderFactory: FoodOrderFactory

    @Autowired
    protected lateinit var orderMenuFactory: OrderMenuFactory

    @Autowired
    protected lateinit var orderMenuItemFactory: OrderMenuItemFactory

    @Autowired
    protected lateinit var orderIngredientFactory: OrderIngredientFactory

}
