package com.duantn.be_project.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.RoleRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.Role;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class StoreController {
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    // Get All
    @GetMapping("/store")
    public ResponseEntity<List<Store>> getAll(Model model) {
        return ResponseEntity.ok(storeRepository.findAll());
    }

    @GetMapping("/store/checkIdUser/{id}")
    public ResponseEntity<Map<String, Boolean>> checkStoreByUserId(@PathVariable("id") Integer userId) {
        boolean exists = storeRepository.findStoreByIdUser(userId) != null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // Post
    @PostMapping("/store")
    public ResponseEntity<Store> post(@RequestBody Store store) {
        // TODO: process POST request
        if (store.getCreatedtime() == null) {
            store.setCreatedtime(LocalDateTime.now());// Thiết lập thời gian tạo
        }
        // Tìm user
        User user = userRepository.findById(store.getUser().getId()).orElseThrow();
        if (user.getRole().getId() == 3) {
            Role newRole = roleRepository.findById(2).orElseThrow();
            user.setRole(newRole);
        }
        userRepository.save(user); // Cập nhật lại role khi tạo store
        store.setUser(user);// Cập nhật lại user khi tạo store
        Store savedStore = storeRepository.save(store);
        return ResponseEntity.ok(savedStore);
    }

    // Put
    @PutMapping("/store/{id}")
    public ResponseEntity<Store> put(@PathVariable("id") Integer id, @RequestBody Store store) {
        // TODO: process PUT request
        if (!storeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        storeRepository.save(store);
        return ResponseEntity.ok(store);
    }

    // Delete
    @DeleteMapping("/store/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!storeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        storeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
