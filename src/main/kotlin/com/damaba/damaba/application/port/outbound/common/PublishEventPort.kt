package com.damaba.user.application.port.outbound.common

interface PublishEventPort {
    fun publish(event: Any)
}
