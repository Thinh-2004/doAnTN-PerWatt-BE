package com.duantn.be_project.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.RoleRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.Role;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.model.User;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
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
    @Autowired
    UploadImages uploadImages;

    // Get All
    @GetMapping("/store")
    public ResponseEntity<List<Store>> getAll(Model model) {
        return ResponseEntity.ok(storeRepository.findAll());
    }

    // Get All
    @GetMapping("/store/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable("id") Integer storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
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
    public ResponseEntity<String> post(@RequestBody Store store) {
        // TODO: process POST request
        if (storeRepository.existsByNamestoreIgnoreCase(store.getNamestore())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên cửa hàng đã tồn tại!");
        }
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
         storeRepository.save(store);
        return ResponseEntity.ok("savedStore");
    }

    @PutMapping("/store/{id}")
    public ResponseEntity<?> put(@PathVariable("id") Integer id, @RequestPart("store") String storeJson,
            @RequestPart(value = "imgbackgound", required = false) MultipartFile imgbackgound) {
        if (!storeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Store store;
        try {
            store = objectMapper.readValue(storeJson, Store.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Không thể chuyển đổi dữ liệu store: " + e.getMessage());
        }

        store.setId(id);

        if (store.getCreatedtime() == null) {
            store.setCreatedtime(LocalDateTime.now());
        }

        String oldImageUrl = null;

        if (imgbackgound != null && !imgbackgound.isEmpty()) {
            oldImageUrl = storeRepository.findById(id)
                    .map(Store::getImgbackgound)
                    .orElse(null);
            try {
                String imgBgUrl = uploadImages.saveStoreImage(imgbackgound, id);
                store.setImgbackgound(imgBgUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        }else{
            String setOldImageUrl = storeRepository.findById(id).map(Store :: getImgbackgound).orElse(null);
            store.setImgbackgound(setOldImageUrl);

        }

        try {
            Store storeUpdate = storeRepository.save(store);

            if (oldImageUrl != null) {
                String filePath = String.format("src/main/resources/static/files/store/%d/%s", id, oldImageUrl);
                File file = new File(filePath);
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Đã xóa ảnh cũ: " + filePath);
                    } else {
                        System.out.println("Không thể xóa ảnh cũ: " + filePath);
                    }
                } else {
                    System.out.println("Ảnh cũ không tồn tại: " + filePath);
                }
            }

            return ResponseEntity.ok(storeUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật cửa hàng: " + e.getMessage());
        }
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
