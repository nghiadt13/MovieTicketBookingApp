package com.apibackend.AppBackend.auth.service

import com.apibackend.AppBackend.auth.dto.UserDto
import com.apibackend.AppBackend.auth.mapper.UserMapper
import com.apibackend.AppBackend.auth.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository, private val userMapper: UserMapper) {

    fun getAllUsers(): List<UserDto> {
        val users = userRepository.findAllWithRoles()
        return userMapper.toDtoList(users)
    }

    fun getAllActiveUsers(): List<UserDto> {
        val users = userRepository.findAllActiveWithRoles()
        return userMapper.toDtoList(users)
    }

    fun getUserById(id: Long): UserDto? {
        return userRepository.findByIdWithRoles(id).map { userMapper.toDto(it) }.orElse(null)
    }
}
