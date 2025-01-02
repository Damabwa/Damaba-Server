package com.damaba.damaba.application.port.inbound.promotion

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.AddressValidator
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionValidator
import com.damaba.damaba.domain.promotion.constant.EventType
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import java.time.LocalDate

interface PostPromotionUseCase {
    /**
     * 신규 프로모션을 등록합니다.
     *
     * @param command
     * @return 생성된 promotion entity
     */
    fun postPromotion(command: Command): Promotion

    data class Command(
        val authorId: Long,
        val type: PromotionType,
        val eventType: EventType,
        val title: String,
        val content: String,
        val address: Address,
        val externalLink: String?,
        val startedAt: LocalDate?,
        val endedAt: LocalDate?,
        val photographerName: String?,
        val photographerInstagramId: String?,
        val images: List<File>,
        val activeRegions: Set<Region>,
        val hashtags: Set<String>,
    ) {
        init {
            AddressValidator.validate(address)
            PromotionValidator.validateTitle(title)
            PromotionValidator.validateContent(content)
            if (images.isEmpty() || images.size > 10) {
                throw ValidationException("프로모션의 이미지는 최소 1장부터 최대 10장까지 첨부할 수 있습니다.")
            }
            if (activeRegions.isEmpty()) {
                throw ValidationException("활동 지역을 최소 1개 이상 선택해야 합니다.")
            }
        }
    }
}
