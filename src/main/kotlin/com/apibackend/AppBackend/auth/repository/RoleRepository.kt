package com.apibackend.AppBackend.auth.repository

import com.apibackend.AppBackend.auth.model.Role
import com.apibackend.AppBackend.auth.model.UserRole
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    // Dùng native query để cast String sang PostgreSQL enum type
    // Đổi tên method để tránh Spring Data JPA tự generate query
    @Query(
        value = "SELECT * FROM roles WHERE name = CAST(:name AS user_role_enum)",
        nativeQuery = true
    )
    fun findRoleByNameString(@Param("name") name: String): Optional<Role>
}
