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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.Repository.RoleRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.ProductCategory;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    CategoryRepository categoryRepository;

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
    public ResponseEntity<?> post(@RequestBody Store store) {
        // TODO: process POST request
        // Bắt lỗi
        ResponseEntity<String> validateRes = validate(store);
        if (validateRes != null) {
            return validateRes;
        }

        if (storeRepository.existsByNamestoreIgnoreCase(store.getNamestore())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tên cửa hàng đã tồn tại!");
        } else if (!store.getTaxcode().isEmpty()) {
            Integer countTaxCode = storeRepository.checkDuplicate(store.getTaxcode());
            if (countTaxCode > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Mã số thuế không tồn tại hoặc đã được sử dụng ở nơi khác");
            }
        }
        if (store.getCreatedtime() == null) {
            store.setCreatedtime(LocalDateTime.now());// Thiết lập thời gian tạo
        }
        if (store.getTaxcode() == null || store.getTaxcode().isEmpty()) {
            store.setTaxcode(null);
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
            // Bắt lỗi
            ResponseEntity<String> validateRes = validate(store);
            if (validateRes != null) {
                return validateRes;
            }
            Integer countTaxCode = storeRepository.checkDuplicate(store.getTaxcode());
            if (countTaxCode > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã thuế đã được sử dụng");
            }
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
        } else {
            String setOldImageUrl = storeRepository.findById(id).map(Store::getImgbackgound).orElse(null);
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

    // Danh mục trong cửa hàng
    @GetMapping("/CateProductInStore/{id}")
    public ResponseEntity<List<ProductCategory>> getCateProById(@PathVariable("id") Integer idStore) {
        List<ProductCategory> productCategories = categoryRepository.cateProductInStore(idStore);
        return ResponseEntity.ok(productCategories);
    }

    @GetMapping("/business/{taxcode}")
    public ResponseEntity<?> getBusinessInfo(@PathVariable String taxcode) {
        String url = "https://api.vietqr.io/v2/business/" + taxcode;
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Mã không hợp lệ");
        }
    }

    // Bắt lỗi
    public ResponseEntity<String> validate(Store store) {
        // Biểu thức chính quy email
        String patternEmail = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        // Biểu thức chính quy số điện thoại
        String patternPhone = "0[0-9]{9}";
        // Biểu thức chính quy căn cước
        String patternCccd = "^[0-9]{9,12}$";

        // Tên cửa hàng
        if (store.getNamestore().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống tên cửa hàng");
        } else if (store.getNamestore().length() < 10) {
            return ResponseEntity.badRequest().body("Tên không hợp lệ");
        }

        // Địa chỉ
        if (store.getAddress().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống địa chỉ");
        }
        // email
        if (store.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống email");
        } else if (!store.getEmail().matches(patternEmail)) {
            return ResponseEntity.badRequest().body("Email không hợp lệ");
        }

        // Số điện thoại
        if (store.getPhone().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được trống số điện thoại");
        } else if (!store.getPhone().matches(patternPhone)) {
            return ResponseEntity.badRequest().body("Số điện thoại không hợp lệ");
        }

        // Căn cước công dân
        if (store.getCccdnumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống số căn cước công dân");
        } else if (!store.getCccdnumber().matches(patternCccd)) {
            return ResponseEntity.badRequest().body("Số căn cước không hợp lệ");
        }

        // Mã sô thuế
        if (store.getTaxcode() == null) {
            return null;
        } else if (!String.valueOf(store.getTaxcode()).matches("^\\d{10}$|^\\d{13}$") && !store.getTaxcode().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã số thuế không hợp lệ");
        }

        return null;
    }

}
