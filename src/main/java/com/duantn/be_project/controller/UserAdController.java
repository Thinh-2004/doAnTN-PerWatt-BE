package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.duantn.be_project.Repository.UserAdRepository;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class UserAdController {

    @Autowired
    private UserAdRepository userAdRepository;

    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/total-users")
    public ResponseEntity<List<Map<String, Object>>> getTotalUsers() {
        List<Map<String, Object>> results = userAdRepository.findTotalUsers();
        return ResponseEntity.ok(results);
    }
}
