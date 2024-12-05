package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.RolePermission;

public interface RolePermissionReponsitory extends JpaRepository<RolePermission, Integer> {

    @Query("""
            select rp from RolePermission rp where rp.permission.id not in (14,15)
            """)
    List<RolePermission> listRolePermissions();
}
