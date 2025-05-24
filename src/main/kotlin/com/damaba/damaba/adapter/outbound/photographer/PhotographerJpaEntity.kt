package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.region.Region
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Table(name = "photographer")
@Entity
class PhotographerJpaEntity(
    @Id @Column(name = "user_id", nullable = false)
    val userId: Long,
    contactLink: String?,
    description: String?,
    address: PhotographerAddressJpaEmbeddable?,
) : BaseJpaTimeEntity() {
    @Column(name = "contact_link", nullable = true)
    var contactLink: String? = contactLink
        private set

    @Column(name = "description", length = 500, nullable = true)
    var description: String? = description
        private set

    @Embedded
    var address: PhotographerAddressJpaEmbeddable? = address
        private set

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var mainPhotographyTypes: MutableSet<PhotographerPhotographyTypeJpaEntity> = mutableSetOf()
        private set

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.PERSIST])
    private var _portfolio: MutableList<PhotographerPortfolioImageJpaEntity> = mutableListOf()

    val portfolio: List<PhotographerPortfolioImageJpaEntity>
        get() = _portfolio.filter { it.deletedAt == null }

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var activeRegions: MutableSet<PhotographerActiveRegionJpaEntity> = mutableSetOf()
        private set

    fun toPhotographer(userJpaEntity: UserJpaEntity) = Photographer(
        id = this.userId,
        type = userJpaEntity.type,
        roles = userJpaEntity.roles,
        loginType = userJpaEntity.loginType,
        oAuthLoginUid = userJpaEntity.oAuthLoginUid,
        nickname = userJpaEntity.nickname,
        profileImage = userJpaEntity.profileImage?.toImage(),
        gender = userJpaEntity.gender,
        instagramId = userJpaEntity.instagramId,
        contactLink = this.contactLink,
        description = this.description,
        address = this.address?.toAddress(),
        mainPhotographyTypes = this.mainPhotographyTypes.map { it.photographyType }.toSet(),
        portfolio = this.portfolio.map { it.toImage() },
        activeRegions = this.activeRegions.map { it.toRegion() }.toSet(),
    )

    fun update(photographer: Photographer) {
        this.contactLink = photographer.contactLink
        this.description = photographer.description
        this.address = photographer.address?.let { PhotographerAddressJpaEmbeddable.from(it) }
        updateMainPhotographyTypes(photographer.mainPhotographyTypes)
        updateActiveRegions(photographer.activeRegions)
        updatePortfolio(photographer.portfolio)
    }

    private fun updateMainPhotographyTypes(mainPhotographyTypes: Set<PhotographyType>) {
        this.mainPhotographyTypes.removeIf { it.photographyType !in mainPhotographyTypes }

        val existingTypes = this.mainPhotographyTypes.map { it.photographyType }.toSet()
        val toAddTypes = mainPhotographyTypes - existingTypes
        this.mainPhotographyTypes.addAll(toAddTypes.map { PhotographerPhotographyTypeJpaEntity(this, it) })
    }

    private fun updateActiveRegions(activeRegions: Set<Region>) {
        this.activeRegions.removeIf { it.toRegion() !in activeRegions }

        val existingRegions = this.activeRegions.map { it.toRegion() }.toSet()
        val toAddRegions = activeRegions - existingRegions
        this.activeRegions.addAll(toAddRegions.map { PhotographerActiveRegionJpaEntity.from(this, it) })
    }

    private fun updatePortfolio(portfolio: List<Image>) {
        val portfolioUrls = portfolio.map { it.url }
        this._portfolio.filter { !it.isDeleted() }.forEach {
            if (it.url !in portfolioUrls) {
                it.delete()
            }
        }

        val existingPortfolioMap = this._portfolio.associateBy { it.url }
        val newPortfolio = portfolio.map { image ->
            existingPortfolioMap[image.url] ?: PhotographerPortfolioImageJpaEntity.from(this, image)
        }
        this._portfolio.clear()
        this._portfolio.addAll(newPortfolio)
    }

    companion object {
        fun from(photographer: Photographer): PhotographerJpaEntity {
            val photographerJpaEntity = PhotographerJpaEntity(
                userId = photographer.id,
                contactLink = photographer.contactLink,
                description = photographer.description,
                address = photographer.address?.let { PhotographerAddressJpaEmbeddable.from(it) },
            )
            photographerJpaEntity.mainPhotographyTypes.addAll(
                photographer.mainPhotographyTypes.map {
                    PhotographerPhotographyTypeJpaEntity(photographerJpaEntity, it)
                },
            )
            photographerJpaEntity._portfolio.addAll(
                photographer.portfolio.map { PhotographerPortfolioImageJpaEntity.from(photographerJpaEntity, it) },
            )
            photographerJpaEntity.activeRegions.addAll(
                photographer.activeRegions.map { PhotographerActiveRegionJpaEntity.from(photographerJpaEntity, it) },
            )
            return photographerJpaEntity
        }
    }
}
