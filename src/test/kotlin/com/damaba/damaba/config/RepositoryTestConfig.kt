package com.damaba.damaba.config

import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.springframework.context.annotation.Import

@Import(JpaConfig::class, KotlinJdslAutoConfiguration::class)
class RepositoryTestConfig
