package com.damaba.damaba.adapter.outbound.photographer

import com.damaba.damaba.adapter.outbound.common.AddressJpaEmbeddable
import com.damaba.damaba.adapter.outbound.common.BaseJpaTimeEntity
import com.damaba.damaba.domain.common.PhotographyType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
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
    mainPhotographyTypes: Set<PhotographyType>,
    contactLink: String?,
    description: String?,
    address: AddressJpaEmbeddable?,
    businessSchedule: BusinessScheduleEmbeddable?,
) : BaseJpaTimeEntity() {
    @Convert(converter = MainPhotohraphyTypesConverter::class)
    @Column(name = "main_photography_type", nullable = false)
    var mainPhotographyTypes: Set<PhotographyType> = mainPhotographyTypes
        private set

    @Column(name = "contact_link", nullable = false)
    var contactLink: String? = contactLink
        private set

    @Column(name = "description", length = 500, nullable = true)
    var description: String? = description
        private set

    @Embedded
    var address: AddressJpaEmbeddable? = address
        private set

    @Embedded
    var businessSchedule: BusinessScheduleEmbeddable? = businessSchedule
        private set

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.PERSIST])
    var portfolio: MutableList<PhotographerPortfolioImageJpaEntity> = mutableListOf()
        private set

    @OneToMany(mappedBy = "photographer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var activeRegions: MutableSet<PhotographerActiveRegionJpaEntity> = mutableSetOf()
        private set
}
