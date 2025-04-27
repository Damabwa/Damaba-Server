package com.damaba.damaba.domain.file

enum class FileType(val uploadPath: String) {
    USER_PROFILE_IMAGE("user/profile/"),
    PHOTOGRAPHER_PORTFOLIO_IMAGE("photographer/portfolio/"),
    PROMOTION_IMAGE("promotion/"),
}
