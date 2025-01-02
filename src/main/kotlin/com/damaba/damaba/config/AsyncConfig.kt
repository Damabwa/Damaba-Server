package com.damaba.damaba.config

import com.damaba.damaba.logger.MdcThreadPoolTaskDecorator
import com.damaba.damaba.property.ThreadPoolProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class AsyncConfig {
    @Bean
    fun threadPoolTaskExecutor(threadPoolProperties: ThreadPoolProperties): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = threadPoolProperties.corePoolSize
        executor.maxPoolSize = threadPoolProperties.maxPoolSize
        executor.queueCapacity = threadPoolProperties.queueCapacity
        executor.setTaskDecorator(MdcThreadPoolTaskDecorator())
        executor.setThreadNamePrefix("damaba-async-")
        executor.initialize()
        return executor
    }
}
