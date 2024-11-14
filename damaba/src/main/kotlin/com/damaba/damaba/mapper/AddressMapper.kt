package com.damaba.damaba.mapper

import com.damaba.damaba.adapter.inbound.common.dto.AddressRequest
import com.damaba.damaba.adapter.inbound.common.dto.AddressResponse
import com.damaba.damaba.adapter.outbound.promotion.PromotionAddressJpaEmbeddable
import com.damaba.damaba.domain.common.Address
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface AddressMapper {
    fun toAddressResponse(address: Address): AddressResponse

    fun toAddress(addressJpaEmbeddable: AddressRequest): Address

    fun toAddress(promotionAddressJpaEmbeddable: PromotionAddressJpaEmbeddable): Address

    fun toAddressJpaEmbeddable(address: Address): PromotionAddressJpaEmbeddable

    companion object {
        val INSTANCE: AddressMapper = Mappers.getMapper(AddressMapper::class.java)
    }
}
