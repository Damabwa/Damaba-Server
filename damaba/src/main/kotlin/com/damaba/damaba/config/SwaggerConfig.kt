package com.damaba.damaba.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

@Configuration
class SwaggerConfig(private val resourceLoader: ResourceLoader) {
    companion object {
        private const val API_DOC_DESCRIPTION_FILE_PATH = "classpath:api-doc-description.html"
    }

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Damaba 통합 API Server")
                    .description(loadApiDoc()),
            )
            .components(
                Components().addSecuritySchemes(
                    "access-token",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT"),
                ),
            )

    private fun loadApiDoc(): String {
        val resource = resourceLoader.getResource(API_DOC_DESCRIPTION_FILE_PATH)
        return resource.inputStream.bufferedReader().use { it.readText() }
    }
}
