package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.Address
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region
import com.damaba.damaba.domain.user.constant.Gender
import com.damaba.damaba.domain.user.constant.LoginType
import com.damaba.damaba.domain.user.constant.UserRoleType
import com.damaba.damaba.domain.user.constant.UserType

data class PhotographerDetail(
    val id: Long,
    val type: UserType,
    val roles: Set<UserRoleType>,
    val loginType: LoginType,
    val nickname: String,
    val profileImage: Image?,
    val gender: Gender,
    val instagramId: String?,
    val mainPhotographyTypes: Set<PhotographyType>,
    val contactLink: String?,
    val description: String?,
    val address: Address?,
    val portfolio: List<Image>,
    val activeRegions: Set<Region>,
    val saveCount: Int,
    val isSaved: Boolean,
)
