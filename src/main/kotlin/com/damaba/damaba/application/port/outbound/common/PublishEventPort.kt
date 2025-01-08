package com.damaba.damaba.application.port.outbound.common

interface PublishEventPort {
    fun publish(event: Any)
}
