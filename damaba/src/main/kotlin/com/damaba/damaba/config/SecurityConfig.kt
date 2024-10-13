package com.damaba.damaba.config

import com.damaba.common_exception.CustomExceptionType
import com.damaba.common_logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {
    companion object {
        // HTTP method 상관 없이, endpoint에 대해 허용
        val AUTH_WHITE_PATHS = listOf(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health",
        )

        // 특정 endpoint, HTTP method에 대해서만 허용
        val AUTH_WHITE_LIST = mapOf(
            "/api/v*/auth/login" to HttpMethod.POST,
        )
    }

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        authFilter: AuthFilter,
        customAccessDeniedHandler: CustomAccessDeniedHandler,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
        @Value("\${damaba.web-url}") damabaWebUrl: String,
    ): SecurityFilterChain = httpSecurity
        .csrf { it.disable() }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .cors { it.configurationSource(corsConfigurationSource(damabaWebUrl)) }
        .authorizeHttpRequests { auth ->
            auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            AUTH_WHITE_PATHS.forEach { authWhitePath -> auth.requestMatchers(authWhitePath).permitAll() }
            AUTH_WHITE_LIST.forEach { (path, httpMethod) -> auth.requestMatchers(httpMethod, path).permitAll() }
            auth.anyRequest().authenticated()
        }
        .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter::class.java)
        .exceptionHandling { exceptionHandlingConfigurer ->
            exceptionHandlingConfigurer
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint)
        }.build()

    private fun corsConfigurationSource(damabaWebUrl: String): CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = listOf(damabaWebUrl)
        corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        corsConfig.allowedHeaders = listOf("*")
        corsConfig.exposedHeaders = listOf("*")
        corsConfig.allowCredentials = true
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }
    }

    @Component
    class CustomAccessDeniedHandler(private val mapper: ObjectMapper) : AccessDeniedHandler {
        override fun handle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            accessDeniedException: AccessDeniedException,
        ) {
            Logger.warn("CustomAccessDeniedHandler.handle() ex=${accessDeniedException.message}")
            response.status = HttpStatus.FORBIDDEN.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "utf-8"
            response.writer.write(
                mapper.writeValueAsString(
                    GlobalExceptionHandler.ErrorResponse(
                        code = CustomExceptionType.ACCESS_DENIED.code,
                        message = "${CustomExceptionType.ACCESS_DENIED.message} ${accessDeniedException.message}",
                    ),
                ),
            )
        }
    }

    @Component
    class CustomAuthenticationEntryPoint(private val mapper: ObjectMapper) : AuthenticationEntryPoint {
        override fun commence(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authenticationException: AuthenticationException,
        ) {
            Logger.warn("CustomAuthenticationEntryPoint.commence() ex=${authenticationException.message}")
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "utf-8"
            response.writer.write(
                mapper.writeValueAsString(
                    GlobalExceptionHandler.ErrorResponse(
                        code = CustomExceptionType.INVALID_AUTH_CREDENTIALS.code,
                        message = "${CustomExceptionType.INVALID_AUTH_CREDENTIALS.message} ${authenticationException.message}",
                    ),
                ),
            )
        }
    }
}