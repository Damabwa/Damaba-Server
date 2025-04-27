package com.damaba.damaba.config

import com.damaba.damaba.domain.user.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = ["com.damaba.damaba"])
@Configuration
class JpaConfig {
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
