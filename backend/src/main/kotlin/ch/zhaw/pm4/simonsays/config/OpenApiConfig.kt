package ch.zhaw.pm4.simonsays.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("SimonSays")
                .version("1.0.0")

        )
}

