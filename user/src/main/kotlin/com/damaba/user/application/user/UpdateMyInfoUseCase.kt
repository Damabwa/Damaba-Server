package com.damaba.user.application.user

import com.damaba.user.domain.file.UploadFile
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.UserService
import com.damaba.user.domain.user.constant.Gender
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import com.damaba.user.domain.user.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdateMyInfoUseCase(private val userService: UserService) {
    /**
     * 유저 정보를 수정한다.
     *
     * @param command
     * @return 수정된 유저 정보
     * @throws UserNotFoundException `userId`와 일치하는 유저 정보를 찾지 못한 경우
     * @throws NicknameAlreadyExistsException `nickname`이 이미 사용중인 닉네임인 경우
     */
    @Transactional
    operator fun invoke(command: Command): User =
        userService.updateUserInfo(
            userId = command.userId,
            nickname = command.nickname,
            gender = command.gender,
            birthDate = command.birthDate,
            instagramId = command.instagramId,
            profileImage = command.profileImage,
        )

    data class Command(
        val userId: Long,
        val nickname: String?,
        val gender: Gender?,
        val birthDate: LocalDate?,
        val instagramId: String?,
        val profileImage: UploadFile?,
    )
}
