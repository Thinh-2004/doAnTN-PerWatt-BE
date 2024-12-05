package com.duantn.be_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.PermissionRepository;
import com.duantn.be_project.model.Permission;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@CrossOrigin("*")
public class PermissionController {
    @Autowired
    PermissionRepository permissionRepository;

    @GetMapping("list/permission")
    public ResponseEntity<List<Permission>> getAll() {
        List<Permission> permissions = permissionRepository.findAllForAdmin();
        return ResponseEntity.ok(permissions);
    }

}
