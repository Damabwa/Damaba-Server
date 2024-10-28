package com.damaba.damaba.application.service.promotion

import com.damaba.common_file.application.port.outbound.UploadFilesPort
import com.damaba.common_file.domain.FileUploadRollbackEvent
import com.damaba.damaba.application.port.inbound.promotion.GetPromotionDetailUseCase
import com.damaba.damaba.application.port.inbound.promotion.PostPromotionUseCase
import com.damaba.damaba.application.port.outbound.GetPromotionPort
import com.damaba.damaba.application.port.outbound.common.PublishEventPort
import com.damaba.damaba.application.port.outbound.promotion.SavePromotionPort
import com.damaba.damaba.domain.promotion.Promotion
import com.damaba.damaba.domain.promotion.PromotionActiveRegion
import com.damaba.damaba.domain.promotion.PromotionImage
import com.damaba.damaba.domain.promotion.constant.PromotionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionService(
    private val getPromotionPort: GetPromotionPort,
    private val savePromotionPort: SavePromotionPort,
    private val uploadFilesPort: UploadFilesPort,
    private val publishEventPort: PublishEventPort,
) : GetPromotionDetailUseCase,
    PostPromotionUseCase {

    @Transactional(readOnly = true)
    override fun getPromotionDetail(promotionId: Long): Promotion =
        getPromotionPort.getById(promotionId)

    @Transactional
    override fun postPromotion(command: PostPromotionUseCase.Command): Promotion {
        val uploadedFiles = uploadFilesPort.upload(command.images, PROMOTION_IMAGE_UPLOAD_PATH)
        val uploadedPromotionImages =
            uploadedFiles.map { uploadedFile -> PromotionImage(uploadedFile.name, uploadedFile.url) }

        return runCatching {
            savePromotionPort.save(createPromotionDomainEntity(command, uploadedPromotionImages))
        }.onFailure {
            publishEventPort.publish(FileUploadRollbackEvent(uploadedFiles = uploadedFiles))
        }.getOrThrow()
    }

    private fun createPromotionDomainEntity(
        command: PostPromotionUseCase.Command,
        promotionImages: List<PromotionImage>,
    ): Promotion = Promotion.create(
        authorId = command.authorId,
        type = command.type,
        eventType = if (command.type == PromotionType.EVENT) command.eventType else null,
        title = command.title,
        content = command.content,
        address = command.address,
        externalLink = command.externalLink,
        startedAt = command.startedAt,
        endedAt = command.endedAt,
        photographerName = command.photographerName,
        photographerInstagramId = command.photographerInstagramId,
        images = promotionImages,
        activeRegions = command.activeRegions.map { region ->
            PromotionActiveRegion(region.category, region.name)
        }.toSet(),
        hashtags = command.hashtags,
    )

    companion object {
        private const val PROMOTION_IMAGE_UPLOAD_PATH = "promotion/"
    }
}
