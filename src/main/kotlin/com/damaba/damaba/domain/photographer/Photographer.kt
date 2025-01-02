package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.User
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.constant.LoginType
import com.damaba.user.domain.user.constant.UserRoleType
import com.damaba.user.domain.user.constant.UserType
import java.util.Objects

class Photographer(
    id: Long,
    type: UserType,
    roles: Set<UserRoleType>,
    loginType: LoginType,
    oAuthLoginUid: String,
    nickname: String,
    profileImage: Image,
    gender: Gender,
    instagramId: String?,
    mainPhotographyTypes: Set<PhotographyType>,
    contactLink: String?,
    description: String?,
    address: Address?,
    businessSchedule: BusinessSchedule?,
    portfolio: List<Image>,
    activeRegions: Set<Region>,
) : User(id, loginType, oAuthLoginUid, type, roles, nickname, profileImage, gender, instagramId) {
    var mainPhotographyTypes: Set<PhotographyType> = mainPhotographyTypes
        private set

    var contactLink: String? = contactLink
        private set

    var description: String? = description
        private set

    var address: Address? = address
        private set

    var businessSchedule: BusinessSchedule? = businessSchedule
        private set

    var portfolio: List<Image> = portfolio
        private set

    var activeRegions: Set<Region> = activeRegions
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Photographer) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    fun registerPhotographer(
        nickname: String,
        gender: Gender,
        instagramId: String?,
        profileImage: Image,
        mainPhotographyTypes: Set<PhotographyType>,
        activeRegions: Set<Region>,
    ) {
        this.type = UserType.PHOTOGRAPHER
        this.roles += UserRoleType.PHOTOGRAPHER
        this.nickname = nickname
        this.gender = gender
        this.instagramId = instagramId
        this.profileImage = profileImage
        this.mainPhotographyTypes = mainPhotographyTypes
        this.activeRegions = activeRegions
    }

    companion object {
        fun create(user: User): Photographer = Photographer(
            id = user.id,
            type = user.type,
            roles = user.roles,
            loginType = user.loginType,
            oAuthLoginUid = user.oAuthLoginUid,
            nickname = user.nickname,
            profileImage = user.profileImage,
            gender = user.gender,
            instagramId = user.instagramId,
            mainPhotographyTypes = setOf(),
            contactLink = null,
            description = null,
            address = null,
            businessSchedule = null,
            portfolio = listOf(),
            activeRegions = setOf(),
        )
    }
}
