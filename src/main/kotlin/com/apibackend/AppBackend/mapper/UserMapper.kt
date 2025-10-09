package com.apibackend.AppBackend.mapper

import com.apibackend.AppBackend.dto.UserDto
import com.apibackend.AppBackend.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {

    @Mapping(
            target = "roles",
            expression =
                    "java(user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()))"
    )
    fun toDto(user: User): UserDto

    fun toDtoList(users: List<User>): List<UserDto>
}
