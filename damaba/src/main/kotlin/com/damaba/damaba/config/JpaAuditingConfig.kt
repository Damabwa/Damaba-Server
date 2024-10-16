package com.damaba.damaba.config

import com.damaba.user.domain.user.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
    @Bean
    fun auditorAware(): AuditorAware<Long> = AuditorAware {
        val principal: Optional<Any> = Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)

        if (principal.isEmpty || principal.get() == "anonymousUser") {
            return@AuditorAware Optional.empty()
        }

        return@AuditorAware principal.map { it as User }.map { it.id }
    }
}
