package com.damaba.damaba.application.port.inbound.photographer

import com.damaba.damaba.domain.common.PhotographyType
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerValidator
import com.damaba.damaba.domain.region.Region
import com.damaba.user.domain.user.UserValidator
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserAlreadyRegisteredException
import com.damaba.user.domain.user.exception.UserNotFoundException

interface RegisterPhotographerUseCase {
    /**
     * 사진작가를 등록한다. 즉, 사진작가의 등록 정보를 수정한다.
     * '등록 정보'란 회원가입 시 유저(사진작가)에게 입력받는 정보를 의미한다.
     *
     * @param command
     * @return 등록된 사진작가
     * @throws UserNotFoundException `userId`에 해당하는 유저를 찾을 수 없는 경우
     * @throws UserAlreadyRegisteredException 이미 등록된 유저인 경우
     * @throws NicknameAlreadyExistsException `nickname`을 다른 유저가 이미 사용중인 경우
     */
    fun register(command: Command): Photographer

    data class Command(
        val userId: Long,
        val nickname: String,
        val gender: Gender,
        val instagramId: String?,
        val profileImage: Image,
        val mainPhotographyTypes: Set<PhotographyType>,
        val activeRegions: Set<Region>,
    ) {
        init {
            PhotographerValidator.validateNickname(nickname)
            if (instagramId != null) UserValidator.validateInstagramId(instagramId)
            PhotographerValidator.validateMainPhotographyTypes(mainPhotographyTypes)
            PhotographerValidator.validateActiveRegions(activeRegions)
        }
    }
}
