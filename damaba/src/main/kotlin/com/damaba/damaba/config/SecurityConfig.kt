package com.damaba.damaba.config

import com.damaba.common_logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
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
class SecurityConfig(private val env: Environment) {
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
            "/api/v*/users/nicknames/existence" to HttpMethod.GET,
        )
    }

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        authFilter: AuthFilter,
        customAccessDeniedHandler: CustomAccessDeniedHandler,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
        @Value("\${damaba.web-url}") damabaWebUrl: String,
        @Value("\${damaba.server-url}") damabaServerUrl: String,
    ): SecurityFilterChain = httpSecurity
        .csrf { it.disable() }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .cors { it.configurationSource(corsConfigurationSource(listOf(damabaWebUrl, damabaServerUrl))) }
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

    private fun corsConfigurationSource(allowedOrigins: List<String>): CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        if (env.activeProfiles.contains("prod")) {
            corsConfig.allowCredentials = true
            corsConfig.allowedOrigins = allowedOrigins
        } else {
            corsConfig.allowCredentials = false
            corsConfig.allowedOrigins = listOf("*")
        }
        corsConfig.allowedMethods = listOf(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
        )
        corsConfig.allowedHeaders = listOf("*")
        corsConfig.exposedHeaders = listOf("*")
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
                        code = "ACCESS_DENIED",
                        message = "접근이 거부되었습니다. 접근을 위한 권한을 확인해주세요. ${accessDeniedException.message}",
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
                        code = "INVALID_AUTH_CREDENTIALS",
                        message = "유효하지 않은 자격 증명입니다. 인증/인가를 위한 토큰이 잘못되지는 않았는지 확인해주세요. ${authenticationException.message}",
                    ),
                ),
            )
        }
    }
}
