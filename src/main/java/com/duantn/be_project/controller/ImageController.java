package com.duantn.be_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ImageRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.model.Image;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin("*")
@RestController
public class ImageController {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ServletContext servletContext;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    @GetMapping("imageByProduct/{id}")
    public ResponseEntity<List<Image>> getByIdProduct(@PathVariable("id") Integer id) {
        if (imageRepository.findAllByIdProduct(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageRepository.findAllByIdProduct(id));
    }

    @PreAuthorize("hasAnyAuthority('Seller_Manage_Shop')")
    @DeleteMapping("image/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
        // Tìm hình ảnh trong cơ sở dữ liệu
        Image image = imageRepository.findById(id).orElse(null);

        // Kiểm tra nếu không tìm thấy hình ảnh
        if (image == null || image.getId() == null) {
            return ResponseEntity.notFound().build();
        }

        // Xóa hình ảnh khỏi Firebase Storage
        try {
            // Giải mã URL trước 
            String decodedUrl = java.net.URLDecoder.decode(image.getImagename(),
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

        // Xóa hình ảnh khỏi cơ sở dữ liệu
        try {
            imageRepository.deleteById(id); // Xóa image khỏi cơ sở dữ liệu
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa hình ảnh khỏi cơ sở dữ liệu: " + e.getMessage());
        }

        // Trả về thành công nếu mọi thứ đều ổn
        return ResponseEntity.ok().build();
    }

}
