package com.damaba.damaba.domain.photographer

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.exception.ValidationException
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.region.Region

object PhotographerValidator {
    private val NICKNAME_PATTERN = "^[a-zA-Zㄱ-ㅎ가-힣0-9\\s]{1,15}$".toRegex()
    private const val PORTFOLIO_MAX_SIZE = 10
    private const val DESCRIPTION_MAX_LEN = 500

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

    fun validatePortfolio(portfolio: List<Image>) {
        if (portfolio.isEmpty()) {
            throw ValidationException("포트폴리오는 비어있을 수 없습니다. 최소 한 장 이상의 이미지를 첨부해야 합니다.")
        }
        if (portfolio.size > PORTFOLIO_MAX_SIZE) {
            throw ValidationException("포트폴리오는 최대 10장까지 첨부할 수 있습니다.")
        }
    }

    fun validateDescription(description: String) {
        if (description.isBlank()) {
            throw ValidationException("작가 소개는 비어있을 수 없습니다.")
        }
        if (description.length > DESCRIPTION_MAX_LEN) {
            throw ValidationException("작가 소개는 최대 500자 까지 작성 가능합니다.")
        }
    }
}
