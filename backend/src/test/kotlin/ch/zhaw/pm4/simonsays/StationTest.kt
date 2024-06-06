package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.controller.StationViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapper
import ch.zhaw.pm4.simonsays.api.mapper.StationMapperImpl
import ch.zhaw.pm4.simonsays.api.types.OrderIngredientDTO
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.exception.ResourceInUseException
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.*
import ch.zhaw.pm4.simonsays.service.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StationTest {
    @MockkBean(relaxed = true)
    protected lateinit var menuItemRepository: MenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuItemRepository: OrderMenuItemRepository

    @MockkBean(relaxed = true)
    protected lateinit var eventService: EventService

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientService: OrderIngredientService

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuService: OrderMenuService

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuItemService: OrderMenuItemService

    @MockkBean(relaxed = true)
    protected lateinit var eventMapper: EventMapper

    @MockkBean(relaxed = true)
    protected lateinit var ingredientRepository: IngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var ingredientMapper: IngredientMapper

    @MockkBean(relaxed = true)
    protected lateinit var ingredientService: IngredientService

    @MockkBean(relaxed = true)
    protected lateinit var stationRepository: StationRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMapper: OrderMapper

    @MockkBean(relaxed = true)
    protected lateinit var orderIngredientRepository: OrderIngredientRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderRepository: OrderRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuRepository: OrderMenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var stationViewNamespace: StationViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var assemblyViewNamespace: AssemblyViewNamespace

    private lateinit var stationService: StationService

    @BeforeEach
    fun setup() {
        // Initialization of mocks
        menuItemRepository = mockk(relaxed = true)
        eventService = mockk(relaxed = true)
        ingredientRepository = mockk(relaxed = true)
        ingredientMapper = mockk(relaxed = true)
        ingredientService = mockk(relaxed = true)
        eventMapper = mockk(relaxed = true)
        stationRepository = mockk(relaxed = true)
        orderMapper = mockk(relaxed = true)
        orderIngredientRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        orderMenuService = mockk(relaxed = true)
        orderMenuItemService = mockk(relaxed = true)
        orderIngredientService = mockk(relaxed = true)
        orderMenuItemRepository = mockk(relaxed = true)
        orderMenuRepository = mockk(relaxed = true)
        stationViewNamespace = mockk(relaxed = true)
        assemblyViewNamespace = mockk(relaxed = true)

        // Construct the service with the mocked dependencies
        stationService = StationService(
                stationRepository,
                StationMapperImpl(),
                eventService,
                ingredientRepository,
                orderRepository,
                orderMapper,
                orderIngredientService,
                orderMenuItemService,
                orderMenuService,
        )
    }

    @Test
    fun `Test station creation`() {
        every { stationRepository.save(any()) } returns getStation()

        every { ingredientRepository.getReferenceById(any()) } returns getTestIngredient1()

        val stationCreateUpdateDTO = getCreateUpdateStationDTO()
        Assertions.assertEquals(getStationDTO(), stationService.createUpdateStation(stationCreateUpdateDTO, getEvent().id!!))
    }

    @Test
    fun `Test station update`()  {
        every { stationRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getStation())
        every { stationRepository.save(any()) } returns getStation(name = "updated name")
        val stationCreateUpdateDTO = getCreateUpdateStationDTO(name = "updated name")
        Assertions.assertEquals(getStationDTO(name = "updated name"), stationService.createUpdateStation(stationCreateUpdateDTO, getEvent().id!!))
    }

    @Test
    fun `Test station fetching`() {
        every { stationRepository.findAllByEventId(getEvent().id!!) } returns mutableListOf(
                getStation(),
                getStation()
        )
        val stations: List<StationDTO> = stationService.listStations(getEvent().id!!)
        Assertions.assertEquals(2, stations.count())
    }

    @Test
    fun `Test station get`() {
        every { stationRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getStation())
        Assertions.assertEquals(
                getStationDTO(), stationService.getStation(1, getEvent().id!!))
    }


    @Test
    fun `Test station not found`() {
        every { stationRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        val error = Assertions.assertThrows(
                ResourceNotFoundException::class.java
        ) { stationService.getStation(1, 1) }
        Assertions.assertEquals("Station not found with ID: 1", error.message)
    }

    @Test
    fun `Test station deletion`() {
        every { stationRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getStation(ingredients = listOf()))
        Assertions.assertEquals(
                Unit, stationService.deleteStation(1, getEvent().id!!))
    }

    @Test
    fun `Test station deletion not found`() {
        every { stationRepository.findByIdAndEventId(any(), any()) } returns Optional.empty()
        Assertions.assertThrows(
                ResourceNotFoundException::class.java,
                { stationService.getStation(1, getEvent().id!!) },
                "Station not found with ID: 1"
        )
    }

    @Test
    fun `Test retrieve ingredients that need to be produced`() {
        val orderIngredientDTO: OrderIngredientDTO = getOrderIngredientDTO()
        every { stationRepository.findByIdAndEventId(any(), any()) } returns Optional.of(getStation())
        every { orderIngredientService.getOrderIngredientByIngredientIds(any()) } returns listOf(
            getOrderIngredient()
        )
        every { orderMapper.mapOrderIngredientToOrderIngredientDTO(any()) } returns orderIngredientDTO
        Assertions.assertEquals(
                listOf(orderIngredientDTO),
                stationService.getStationView(1, 1)
        )
    }

       @Test
       fun `Test delete Station still in use exception`() {
           every { stationRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getStation(ingredients = listOf(getTestIngredient1())))
           val error = Assertions.assertThrows(
                   ResourceInUseException::class.java)
           { stationService.deleteStation(1, getEvent().id!!) }
           Assertions.assertEquals("Station is used in ingredients and cannot be deleted", error.message)
       }



}