package com.damaba.damaba.mapper

import com.damaba.damaba.application.user.dto.UpdateUserProfileCommand
import com.damaba.damaba.controller.user.response.UserResponse
import com.damaba.damaba.domain.user.User
import com.damaba.damaba.domain.user.UserProfile
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [ImageMapper::class])
interface UserMapper {
    fun toUserResponse(user: User): UserResponse

    fun toUserProfile(command: UpdateUserProfileCommand): UserProfile

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}
