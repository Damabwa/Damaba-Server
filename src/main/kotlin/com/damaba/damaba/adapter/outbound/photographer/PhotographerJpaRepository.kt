package com.damaba.damaba.adapter.outbound.photographer

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface PhotographerJpaRepository :
    JpaRepository<PhotographerJpaEntity, Long>,
    KotlinJdslJpqlExecutor
