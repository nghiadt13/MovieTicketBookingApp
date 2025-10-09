package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.User
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.isActive = true")
    fun findAllActiveWithRoles(): List<User>

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    fun findAllWithRoles(): List<User>

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    fun findByIdWithRoles(id: Long): Optional<User>

    fun findByEmail(email: String): Optional<User>

    fun findByPhoneNumber(phoneNumber: String): Optional<User>
}
