package com.apibackend.AppBackend.payment.mapper

import com.apibackend.AppBackend.payment.dto.PaymentMethodDto
import com.apibackend.AppBackend.payment.model.PaymentIconType
import com.apibackend.AppBackend.payment.model.PaymentMethod
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
interface PaymentMethodMapper {

    @Mapping(source = "iconType", target = "iconType", qualifiedByName = ["iconTypeToString"])
    fun paymentMethodToDto(paymentMethod: PaymentMethod): PaymentMethodDto

    fun paymentMethodsToDtos(paymentMethods: List<PaymentMethod>): List<PaymentMethodDto>

    @Named("iconTypeToString")
    fun iconTypeToString(iconType: PaymentIconType): String = iconType.name
}
