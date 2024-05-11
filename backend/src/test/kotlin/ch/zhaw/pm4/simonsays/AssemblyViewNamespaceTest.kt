package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.api.controller.AssemblyViewNamespace
import ch.zhaw.pm4.simonsays.api.mapper.OrderMapperImpl
import ch.zhaw.pm4.simonsays.repository.OrderMenuItemRepository
import ch.zhaw.pm4.simonsays.repository.OrderMenuRepository
import ch.zhaw.pm4.simonsays.repository.OrderRepository
import ch.zhaw.pm4.simonsays.repository.StationRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AssemblyViewNamespaceTest {

    protected lateinit var assemblyViewNamespace: AssemblyViewNamespace

    @MockkBean(relaxed = true)
    protected lateinit var stationRepository: StationRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderRepository: OrderRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuRepository: OrderMenuRepository

    @MockkBean(relaxed = true)
    protected lateinit var orderMenuitemRepository: OrderMenuItemRepository

    @BeforeEach
    fun setup() {
        stationRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        orderMenuRepository = mockk(relaxed = true)
        orderMenuitemRepository = mockk(relaxed = true)
        assemblyViewNamespace = AssemblyViewNamespace(
                stationRepository,
                orderRepository,
                orderMenuRepository,
                orderMenuitemRepository,
                OrderMapperImpl()
        )
    }

    @ParameterizedTest
    @CsvSource(
            "/socket-api/v1/event/1/station/assembly, 1"
    )
    fun `test namespace pattern matching with valid inputs`(
            input: String,
            expectedEventId: String?,
    ) {
        val matchResult = AssemblyViewNamespace.namespacePattern.matchEntire(input)
        Assertions.assertNotNull(matchResult, "Expected a match for input: $input")
        Assertions.assertEquals(expectedEventId, matchResult!!.groups[1]?.value)
    }
}