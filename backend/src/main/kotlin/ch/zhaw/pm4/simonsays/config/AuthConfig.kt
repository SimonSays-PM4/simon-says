package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.service.EventService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthConfig(
    private val applicationProperties: ApplicationProperties,
    private val eventService: EventService
) {
    // Add URl patterns to filter
    @Bean
    fun adminAuthFilter(): FilterRegistrationBean<AdminAuthFilter> {
        val filterRegistrationBean = FilterRegistrationBean(AdminAuthFilter(applicationProperties))
        filterRegistrationBean.addUrlPatterns("/rest-api/v1/event/admin/*")
        return filterRegistrationBean
    }
    @Bean
    fun eventAuthFilter(): FilterRegistrationBean<EventAuthFilter> {
        val filterRegistrationBean = FilterRegistrationBean(EventAuthFilter(applicationProperties, eventService))
        filterRegistrationBean.addUrlPatterns("/rest-api/v1/event/*")
        return filterRegistrationBean
    }
}