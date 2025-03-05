package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.Photographer

interface UpdatePhotographerPort {
    /**
     * 사진작가를 업데이트한다.
     *
     * @param photographer 업데이트하고자 하는 사진작가 정보가 담긴 객체
     * @return 수정된 사진작가
     */
    fun update(photographer: Photographer): Photographer
}
