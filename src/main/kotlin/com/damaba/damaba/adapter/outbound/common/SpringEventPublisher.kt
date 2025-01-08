package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.application.port.outbound.common.PublishEventPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

// 추후, 분산서버로 전환 시 각 서버에 event publisher 구현체 별도 구현

@Component
class SpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PublishEventPort {
    override fun publish(event: Any) {
        applicationEventPublisher.publishEvent(event)
    }
}
