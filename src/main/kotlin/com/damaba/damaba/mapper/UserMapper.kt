package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.user.dto.UserResponse
import com.damaba.damaba.domain.user.User
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [ImageMapper::class])
interface UserMapper {
    fun toUserResponse(user: User): UserResponse

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}
