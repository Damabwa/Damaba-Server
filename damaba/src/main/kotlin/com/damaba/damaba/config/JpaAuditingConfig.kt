package com.damaba.damaba.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
    @Bean
    fun auditorAware(): AuditorAware<Long> = AuditorAware {
        // TODO: 인증/인가 로직 도입 후 구현 필요
        return@AuditorAware Optional.ofNullable(1L)
    }
}
