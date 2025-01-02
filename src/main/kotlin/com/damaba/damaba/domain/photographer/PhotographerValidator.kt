package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.region.Region

object PhotographerValidator {
    private val NICKNAME_PATTERN = "^[a-zA-Zㄱ-ㅎ가-힣0-9\\s]{1,15}$".toRegex()

    fun validateNickname(nickname: String) {
        if (nickname.isBlank() || !nickname.matches(NICKNAME_PATTERN)) {
            throw ValidationException("유효하지 않은 닉네임입니다. 닉네임은 한글, 영문, 숫자, 공백으로 이루어진 15글자 이내여야 합니다.")
        }
    }

    fun validateMainPhotographyTypes(mainPhotographyTypes: Set<PhotographyType>) {
        if (mainPhotographyTypes.isEmpty()) {
            throw ValidationException("촬영 종류는 최소 한 개 이상 선택해야 합니다.")
        }
        if (mainPhotographyTypes.size > 3) {
            throw ValidationException("촬영 종류는 최대 3개까지 선택 가능합니다.")
        }
    }

    fun validateActiveRegions(activeRegions: Set<Region>) {
        if (activeRegions.isEmpty()) {
            throw ValidationException("활동 지역은 최소 한 개 이상 선택해야 합니다.")
        }
        if (activeRegions.size > 3) {
            throw ValidationException("활동 지역은 최대 3개까지 선택 가능합니다.")
        }
    }
}
