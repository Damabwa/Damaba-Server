package com.damaba.damaba.infrastructure.photographer

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface PhotographerJpaRepository :
    JpaRepository<PhotographerJpaEntity, Long>,
    KotlinJdslJpqlExecutor
