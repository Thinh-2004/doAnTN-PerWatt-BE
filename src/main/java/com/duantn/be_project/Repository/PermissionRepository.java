package com.duantn.be_project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    @Query("""
            select p from Permission p where p.name not in ('Manage_Shop', 'Manage_Buyer')
            """)
    List<Permission> findAllForAdmin();
}
