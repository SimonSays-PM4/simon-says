package ch.zhaw.pm4.simonsays.api.controller.socketio

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext

class WebSocketConfiguratorTest {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var webSocketConfigurator: WebSocketConfigurator

    @BeforeEach
    fun setUp() {
        // Mock the ApplicationContext
        applicationContext = mockk(relaxed = true)

        // Initialize WebSocketConfigurator
        webSocketConfigurator = WebSocketConfigurator(applicationContext)
    }

    @Test
    fun `should retrieve bean from application context`() {
        val expectedBean = MyEndpoint()
        every { applicationContext.getBean(MyEndpoint::class.java) } returns expectedBean

        val actualBean = webSocketConfigurator.getEndpointInstance(MyEndpoint::class.java)

        assertEquals(expectedBean, actualBean, "The returned bean should match the expected bean from the application context.")
    }

    @Test
    fun `should throw exception when bean is not found in application context`() {
        every { applicationContext.getBean(MyEndpoint::class.java) } throws NoSuchBeanDefinitionException(MyEndpoint::class.java)

        assertThrows<NoSuchBeanDefinitionException> {
            webSocketConfigurator.getEndpointInstance(MyEndpoint::class.java)
        }
    }

    class MyEndpoint // Example endpoint class for testing
}
