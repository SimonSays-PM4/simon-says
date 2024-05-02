package ch.zhaw.pm4.simonsays.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig (
       private val applicationProperties: ApplicationProperties
):WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/rest-api/**")
                .allowedOrigins(applicationProperties.url)
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.OPTIONS.name(), HttpMethod.DELETE.name())
                .allowedHeaders(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ORIGIN)
                .allowCredentials(true)
    }
}