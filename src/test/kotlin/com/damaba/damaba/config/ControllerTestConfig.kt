package com.damaba.damaba.config

import io.mockk.mockk
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(
    SecurityConfig::class,
    SecurityConfig.CustomAccessDeniedHandler::class,
    SecurityConfig.CustomAuthenticationEntryPoint::class,
)
@TestConfiguration
@ActiveProfiles("test")
class ControllerTestConfig {
    @Bean
    fun authFilter(): AuthFilter = object : AuthFilter(mockk(), mockk(), mockk()) {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain,
        ) {
            filterChain.doFilter(request, response)
        }
    }
}
