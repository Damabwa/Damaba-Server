package com.damaba.damaba.config

import com.damaba.damaba.application.port.outbound.auth.ParseUserIdFromAuthTokenPort
import com.damaba.damaba.application.port.outbound.auth.ValidateAuthTokenPort
import com.damaba.damaba.application.port.outbound.user.FindUserPort
import com.damaba.damaba.domain.user.exception.UserNotFoundException
import com.damaba.damaba.logger.MdcLogTraceManager
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(
    private val validateAuthTokenPort: ValidateAuthTokenPort,
    private val parseUserIdFromAuthTokenPort: ParseUserIdFromAuthTokenPort,
    private val findUserPort: FindUserPort,
) : OncePerRequestFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        runCatching {
            val accessToken = findAccessTokenFromHeader(request)
            if (accessToken.isNullOrBlank()) {
                // runCatching에 의해 무시되는 exception
                throw AuthenticationCredentialsNotFoundException("Access token does not exist.")
            }

            validateAuthTokenPort.validateAccessToken(accessToken)

            val userId = parseUserIdFromAuthTokenPort.parseUserId(accessToken)
            val user = findUserPort.findById(userId) ?: throw UserNotFoundException()
            MdcLogTraceManager.setRequestUserIdIfAbsent(user.id)

            val authorities = user.roles
                .map { roleType -> "ROLE_${roleType.name}" }
                .map { roleName -> SimpleGrantedAuthority(roleName) }
                .toList()
            val authentication = UsernamePasswordAuthenticationToken(user, "", authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Request의 header에서 token을 읽어온다.
     *
     * @param request Request 객체
     * @return Header에서 추출한 token
     */
    private fun findAccessTokenFromHeader(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null
        }
        return authorizationHeader.substring(BEARER_PREFIX.length)
    }
}
