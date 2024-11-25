package com.duantn.be_project.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.CategoryRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.model.ProductCategory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    // GetAll
    @GetMapping("/category")
    public ResponseEntity<List<ProductCategory>> getAll(Model model) {
        List<ProductCategory> productCategories = categoryRepository.findAllByDESC();
        // productCategories.sort(Comparator.comparing((ProductCategory pc) -> pc.getName()));
        return ResponseEntity.ok(productCategories);
    }

    @GetMapping("/category/hot")
    public ResponseEntity<List<ProductCategory>> getCategory(Model model) {
        List<ProductCategory> productCategories = categoryRepository.sortByPCAZ();
        return ResponseEntity.ok(productCategories);
    }

    // GetAllById
    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/category/{id}")
    public ResponseEntity<ProductCategory> getById(@PathVariable("id") Integer id) {
        ProductCategory productCategory = categoryRepository.findById(id).orElseThrow();
        if (productCategory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productCategory);
    }

    // Post
    @PreAuthorize("hasAnyAuthority('Admin')")
    @PostMapping("/category")
    public ResponseEntity<?> createCategory(
            @RequestParam("name") String name,
            @RequestParam("vat") float vat,
            @RequestParam("imageCateProduct") MultipartFile file) {

        ProductCategory category = new ProductCategory();
        if (file != null && !file.isEmpty()) {

            try {
                // Lưu hình lên firebase
                String imageCategory = firebaseStorageService.uploadToFirebase(file, "categorires");
                // Lưu danh mục sản phẩm vào cơ sở dữ liệu

                category.setName(name);
                category.setVat(vat);
                category.setImagecateproduct(imageCategory);

            } catch (IOException e) {

                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi tải ảnh lên Firebase: " + e.getMessage());
            }
        }
        ProductCategory savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);

    }

    // Put
    @PreAuthorize("hasAnyAuthority('Admin')")
    @PutMapping("category/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("vat") float vat,
            @RequestParam(value = "imageCateProduct", required = false) MultipartFile file) {

        ProductCategory existingCategory = categoryRepository.findById(id).orElseThrow();
        if (existingCategory == null || existingCategory.getId() == null) {
            return ResponseEntity.notFound().build();
        }

        // // Lưu tên ảnh cũ
        String oldImageDetail = existingCategory.getImagecateproduct();
        // Giải mã URL trước
        String decodedUrl = java.net.URLDecoder.decode(oldImageDetail,
                java.nio.charset.StandardCharsets.UTF_8);

        // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
        String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

        // Loại bỏ phần ?alt=media
        int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
        if (queryIndex != -1) {
            filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
        }

        // Kiểm tra nếu có tải lên hình ảnh mới
        if (file != null && !file.isEmpty()) {
            try {
                // Lưu hình ảnh mới lên Firebase và lấy URL
                String newAvatarUrl = firebaseStorageService.uploadToFirebase(file,
                        "categorires");

                // Xóa ảnh cũ trên Firebase nếu có
                if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }

                // Cập nhật avatar mới
                existingCategory.setImagecateproduct(newAvatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lưu hình ảnh: " + e.getMessage());
            }
        } else {
            existingCategory.setImagecateproduct(existingCategory.getImagecateproduct());
        }

        existingCategory.setName(name);
        existingCategory.setVat(vat);
        ProductCategory savedCategory = categoryRepository.save(existingCategory);
        return ResponseEntity.ok(savedCategory);
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        ProductCategory productCategory = categoryRepository.findById(id).orElseThrow();
        if (productCategory == null || productCategory.getId() == null) {
            return ResponseEntity.notFound().build();
        }

        // Xóa hình ảnh khỏi Firebase Storage
        try {
            // Giải mã URL trước
            String decodedUrl = java.net.URLDecoder.decode(productCategory.getImagecateproduct(),
                    java.nio.charset.StandardCharsets.UTF_8);

            // Loại bỏ phần https://firebasestorage.googleapis.com/v0/b/ và lấy phần sau o/
            String filePath = decodedUrl.split("o/")[1]; // Tách phần sau "o/"

            // Loại bỏ phần ?alt=media
            int queryIndex = filePath.indexOf("?"); // Tìm vị trí của dấu hỏi "?"
            if (queryIndex != -1) {
                filePath = filePath.substring(0, queryIndex); // Cắt bỏ phần sau dấu hỏi
            }

            // Gọi Firebase Storage Service để xóa file
            firebaseStorageService.deleteFileFromFirebase(filePath); // Xóa file khỏi Firebase
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa hình ảnh khỏi Firebase: " + e.getMessage());
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
