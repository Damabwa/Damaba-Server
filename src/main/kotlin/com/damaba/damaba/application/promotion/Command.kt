package com.damaba.damaba.application.promotion

import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.promotion.PromotionValidator
import com.damaba.damaba.domain.promotion.constant.PromotionType
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import java.time.LocalDate

data class PostPromotionCommand(
    val authorId: Long,
    val promotionType: PromotionType,
    val title: String,
    val content: String,
    val externalLink: String?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val photographyTypes: Set<PhotographyType>,
    val images: List<File>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
) {
    init {
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

data class SavePromotionCommand(
    val userId: Long,
    val promotionId: Long,
)

data class UpdatePromotionCommand(
    val requestUserId: Long,
    val promotionId: Long,
    val promotionType: PromotionType,
    val title: String,
    val content: String,
    val externalLink: String?,
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val photographyTypes: Set<PhotographyType>,
    val images: List<Image>,
    val activeRegions: Set<Region>,
    val hashtags: Set<String>,
) {
    init {
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

data class DeletePromotionCommand(
    val requestUser: User,
    val promotionId: Long,
)

data class UnsavePromotionCommand(
    val userId: Long,
    val promotionId: Long,
)
