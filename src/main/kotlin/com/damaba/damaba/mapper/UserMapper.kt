package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.user.dto.UserResponse
import com.damaba.damaba.adapter.outbound.user.UserJpaEntity
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaEmbeddable
import com.damaba.damaba.adapter.outbound.user.UserProfileImageJpaEntity
import com.damaba.damaba.domain.file.Image
import com.damaba.damaba.domain.user.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [ImageMapper::class])
interface UserMapper {
    fun toUserResponse(user: User): UserResponse

    @Mapping(source = "OAuthLoginUid", target = "oAuthLoginUid")
    fun toUser(userJpaEntity: UserJpaEntity): User

    fun toImage(userProfileImageJpaEntity: UserProfileImageJpaEntity): Image

    @Mapping(source = "OAuthLoginUid", target = "oAuthLoginUid")
    fun toUserJpaEntity(user: User): UserJpaEntity

    fun toUserProfileImageJpaEmbeddable(image: Image): UserProfileImageJpaEmbeddable

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}
