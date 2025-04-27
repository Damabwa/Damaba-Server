package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerValidator

interface UpdatePhotographerPageUseCase {
    /**
     * `photographerId`에 해당하는 작가의 작가 페이지 정보를 업데이트한다.
     *
     * @param command
     * @return 업데이트된 작가 객체
     */
    fun updatePhotographerPage(command: Command): Photographer

    data class Command(
        val photographerId: Long,
        val portfolio: List<Image>,
        val address: Address?,
        val instagramId: String?,
        val contactLink: String?,
        val description: String,
    ) {
        init {
            PhotographerValidator.validatePortfolio(portfolio)
            PhotographerValidator.validateDescription(description)
        }
    }
}
