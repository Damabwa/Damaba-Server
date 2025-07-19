package com.damaba.damaba.mapper

import com.damaba.damaba.controller.auth.AuthTokenResponse
import com.damaba.damaba.domain.auth.AuthToken
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface AuthTokenMapper {
    fun toAuthTokenResponse(authToken: AuthToken): AuthTokenResponse

    companion object {
        val INSTANCE: AuthTokenMapper = Mappers.getMapper(AuthTokenMapper::class.java)
    }
}
