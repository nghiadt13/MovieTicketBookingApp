package com.apibackend.AppBackend.payment.mapper

import com.apibackend.AppBackend.payment.dto.ComboDto
import com.apibackend.AppBackend.payment.model.Combo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.math.BigDecimal

@Mapper(componentModel = "spring")
interface ComboMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = ["longToString"])
    @Mapping(source = "price", target = "price", qualifiedByName = ["bigDecimalToLong"])
    fun comboToDto(combo: Combo): ComboDto

    fun combosToDtos(combos: List<Combo>): List<ComboDto>

    @Named("longToString")
    fun longToString(id: Long): String = id.toString()

    @Named("bigDecimalToLong")
    fun bigDecimalToLong(price: BigDecimal): Long = price.toLong()
}
