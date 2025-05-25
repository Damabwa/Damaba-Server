package com.damaba.damaba.mapper

import com.damaba.damaba.controller.common.dto.AddressRequest
import com.damaba.damaba.controller.common.dto.AddressResponse
import com.damaba.damaba.domain.common.Address
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface AddressMapper {
    fun toAddressResponse(address: Address): AddressResponse

    fun toAddress(addressJpaEmbeddable: AddressRequest): Address

    companion object {
        val INSTANCE: AddressMapper = Mappers.getMapper(AddressMapper::class.java)
    }
}
