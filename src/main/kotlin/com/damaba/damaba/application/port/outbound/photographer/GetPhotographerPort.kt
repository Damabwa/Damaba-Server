package com.damaba.damaba.application.port.outbound.photographer

import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException

interface GetPhotographerPort {
    /**
     * 사진작가를 단건 조회한다.
     *
     * @param id 조회할 사진작가의 id
     * @return 조회된 사진작가
     * @throws PhotographerNotFoundException id와 일치하는 사진작가가 없는 경우
     */
    fun getById(id: Long): Photographer
}
