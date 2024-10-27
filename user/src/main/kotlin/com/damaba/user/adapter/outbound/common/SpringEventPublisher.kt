package com.damaba.user.adapter.outbound.common

import com.damaba.user.application.port.outbound.common.PublishEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PublishEventPort {
    override fun publish(event: Any) {
        applicationEventPublisher.publishEvent(event)
    }
}
