package com.damaba.user.application.service.user

import com.damaba.user.application.port.inbound.user.CheckNicknameExistenceUseCase
import com.damaba.user.application.port.inbound.user.GetMyInfoUseCase
import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.application.port.outbound.common.PublishEventPort
import com.damaba.user.application.port.outbound.file.UploadFilePort
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.application.port.outbound.user.UpdateUserPort
import com.damaba.user.domain.file.FileUploadRollbackEvent
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val getUserPort: GetUserPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val updateUserPort: UpdateUserPort,
    private val uploadFilePort: UploadFilePort,
    private val publishEventPort: PublishEventPort,
) : GetMyInfoUseCase,
    CheckNicknameExistenceUseCase,
    UpdateMyInfoUseCase {

    @Transactional(readOnly = true)
    override fun getMyInfo(userId: Long): User =
        getUserPort.getById(userId)

    @Transactional(readOnly = true)
    override fun doesNicknameExist(command: CheckNicknameExistenceUseCase.Command): Boolean =
        checkNicknameExistencePort.doesNicknameExist(command.nickname)

    @Transactional
    override fun updateMyInfo(command: UpdateMyInfoUseCase.Command): User {
        if (command.nickname != null && checkNicknameExistencePort.doesNicknameExist(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }

        val user = getUserPort.getById(command.userId)

        val uploadedProfileImage = command.profileImage?.let {
            uploadFilePort.upload(command.profileImage, USER_PROFILE_IMAGE_UPLOAD_PATH)
        }

        user.update(
            nickname = command.nickname,
            gender = command.gender,
            birthDate = command.birthDate,
            instagramId = command.instagramId,
            profileImageUrl = uploadedProfileImage?.url,
        )

        return runCatching {
            updateUserPort.update(user)
        }.onFailure {
            if (uploadedProfileImage != null) {
                publishEventPort.publish(FileUploadRollbackEvent(uploadedFile = uploadedProfileImage))
            }
        }.getOrThrow()
    }

    companion object {
        private const val USER_PROFILE_IMAGE_UPLOAD_PATH = "user/profile-image/"
    }
}
