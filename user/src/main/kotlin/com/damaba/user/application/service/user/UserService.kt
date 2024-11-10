package com.damaba.user.application.service.user

import com.damaba.common_file.domain.DeleteFileEvent
import com.damaba.user.application.port.inbound.user.CheckNicknameExistenceUseCase
import com.damaba.user.application.port.inbound.user.GetMyInfoUseCase
import com.damaba.user.application.port.inbound.user.UpdateMyInfoUseCase
import com.damaba.user.application.port.outbound.common.PublishEventPort
import com.damaba.user.application.port.outbound.user.CheckNicknameExistencePort
import com.damaba.user.application.port.outbound.user.GetUserPort
import com.damaba.user.application.port.outbound.user.UpdateUserPort
import com.damaba.user.domain.user.User
import com.damaba.user.domain.user.exception.NicknameAlreadyExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val getUserPort: GetUserPort,
    private val checkNicknameExistencePort: CheckNicknameExistencePort,
    private val updateUserPort: UpdateUserPort,
    private val publishEventPort: PublishEventPort,
) : GetMyInfoUseCase,
    CheckNicknameExistenceUseCase,
    UpdateMyInfoUseCase {

    @Transactional(readOnly = true)
    override fun getMyInfo(userId: Long): User =
        getUserPort.getById(userId)

    @Transactional(readOnly = true)
    override fun doesNicknameExist(query: CheckNicknameExistenceUseCase.Query): Boolean =
        checkNicknameExistencePort.doesNicknameExist(query.nickname)

    @Transactional
    override fun updateMyInfo(command: UpdateMyInfoUseCase.Command): User {
        val user = getUserPort.getById(command.userId)

        val isNicknameNew = user.nickname != command.nickname
        if (isNicknameNew && checkNicknameExistencePort.doesNicknameExist(command.nickname)) {
            throw NicknameAlreadyExistsException(command.nickname)
        }

        if (user.profileImage != command.profileImage) {
            publishEventPort.publish(DeleteFileEvent(url = user.profileImage.url))
        }

        user.update(
            nickname = command.nickname,
            gender = command.gender,
            instagramId = command.instagramId,
            profileImage = command.profileImage,
        )
        return updateUserPort.update(user)
    }
}
