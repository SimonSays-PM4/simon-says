package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.mapper.EventMapper
import ch.zhaw.pm4.simonsays.api.mapper.IngredientMapper
import ch.zhaw.pm4.simonsays.api.mapper.StationMapperImpl
import ch.zhaw.pm4.simonsays.api.types.StationDTO
import ch.zhaw.pm4.simonsays.exception.ResourceNotFoundException
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import ch.zhaw.pm4.simonsays.repository.MenuItemRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
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
    protected lateinit var eventService: EventService

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

        // Construct the service with the mocked dependencies
        stationService = StationServiceImpl(
                stationRepository,
                StationMapperImpl(),
                eventService,
                ingredientRepository
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
        every { stationRepository.findByIdAndEventId(1, getEvent().id!!) } returns Optional.of(getStation())
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

}