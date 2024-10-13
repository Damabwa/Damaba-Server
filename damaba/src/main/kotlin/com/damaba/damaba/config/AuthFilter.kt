package com.damaba.damaba.config

import com.damaba.damaba.config.SecurityConfig.Companion.AUTH_WHITE_LIST
import com.damaba.damaba.config.SecurityConfig.Companion.AUTH_WHITE_PATHS
import com.damaba.user.domain.auth.AuthTokenService
import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.exception.UserNotFoundException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(
    private val authTokenService: AuthTokenService,
    private val userService: UserService,
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private val pathMatcher = AntPathMatcher()
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        runCatching {
            if (isAuthRequired(request.requestURI, request.method)) {
                val accessToken = findAccessTokenFromHeader(request)
                if (accessToken.isNullOrBlank()) {
                    throw AuthenticationCredentialsNotFoundException("Access token does not exist.")
                }

                authTokenService.validate(accessToken)

                val userId = authTokenService.parseUserId(accessToken)
                val user = userService.findUserById(userId) ?: throw UserNotFoundException()
                val authorities = user.roles
                    .map { roleType -> "ROLE_${roleType.name}" }
                    .map { roleName -> SimpleGrantedAuthority(roleName) }
                    .toList()
                val authentication = UsernamePasswordAuthenticationToken(user, "", authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }

    /**
     * 인증/인가 권한이 필요한 요청인지 확인한다.
     *
     * @param uri request uri
     * @param method request http method
     * @return 인증이 필요한 요청이라면 true를, 필요하지 않은 요청이라면 false를 반환한다.
     */
    private fun isAuthRequired(uri: String, method: String): Boolean {
        if (AUTH_WHITE_PATHS.any { authWhitePath -> pathMatcher.match(authWhitePath, uri) }) {
            return false
        }
        if (AUTH_WHITE_LIST.any { (path, httpMethod) -> pathMatcher.match(path, uri) && httpMethod.name() == method }) {
            return false
        }
        return true
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
