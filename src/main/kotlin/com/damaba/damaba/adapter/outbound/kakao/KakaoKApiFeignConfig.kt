package com.damaba.damaba.adapter.outbound.kakao

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KakaoKApiFeignConfig {
    @Bean
    fun kakaoKApiFeignErrorDecoder(mapper: ObjectMapper): KakaoKApiFeignErrorDecoder =
        KakaoKApiFeignErrorDecoder(mapper)
}
