package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.Role
import com.apibackend.AppBackend.model.UserRole
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: UserRole): Optional<Role>
}
