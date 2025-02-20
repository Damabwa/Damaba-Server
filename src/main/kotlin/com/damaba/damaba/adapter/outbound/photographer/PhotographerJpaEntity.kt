package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.domain.photographer.Photographer
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

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.ALL])
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
        profileImage = userJpaEntity.profileImage.toImage(),
        gender = userJpaEntity.gender,
        instagramId = userJpaEntity.instagramId,
        contactLink = this.contactLink,
        description = this.description,
        address = this.address?.toAddress(),
        mainPhotographyTypes = this.mainPhotographyTypes.map { it.photographyType }.toSet(),
        portfolio = this.portfolio.map { it.toImage() },
        activeRegions = this.activeRegions.map { it.toRegion() }.toSet(),
    )

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
