package com.apibackend.AppBackend.auth.repository

import com.apibackend.AppBackend.auth.model.Role
import com.apibackend.AppBackend.auth.model.UserRole
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: UserRole): Optional<Role>
}
