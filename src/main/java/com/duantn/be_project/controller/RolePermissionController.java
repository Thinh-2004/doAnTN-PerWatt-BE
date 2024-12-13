package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.RolePermissionReponsitory;
import com.duantn.be_project.model.RolePermission;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@CrossOrigin("*")
public class RolePermissionController {

    @Autowired
    RolePermissionReponsitory rolePermissionReponsitory;

    @PreAuthorize("hasAnyAuthority('Admin_All_Function', 'Admin_Manage_Support')")
    @GetMapping("/role/permission/list")
    public ResponseEntity<List<RolePermission>> getMethodName() {
        List<RolePermission> rolePermissions = rolePermissionReponsitory.listRolePermissions();
        return ResponseEntity.ok(rolePermissions);
    }

}
