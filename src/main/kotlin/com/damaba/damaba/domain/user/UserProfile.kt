package com.damaba.damaba.domain.user

import com.damaba.damaba.domain.file.Image

data class UserProfile(
    val nickname: String,
    val instagramId: String?,
    val profileImage: Image?,
)
