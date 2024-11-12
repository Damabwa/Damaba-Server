package com.damaba.damaba.domain.photographer

import com.damaba.common_file.domain.Image
import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.region.Region
import com.damaba.user.domain.user.User
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
    activeRegions: List<Region>,
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

    var activeRegions: List<Region> = activeRegions
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Photographer) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)
}
