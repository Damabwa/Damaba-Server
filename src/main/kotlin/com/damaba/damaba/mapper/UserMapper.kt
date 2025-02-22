package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.user.dto.UserResponse
import com.damaba.damaba.application.port.inbound.user.UpdateUserProfileUseCase
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.UserProfile
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [ImageMapper::class])
interface UserMapper {
    fun toUserResponse(user: User): UserResponse

    fun toUserProfile(command: UpdateUserProfileUseCase.Command): UserProfile

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}
