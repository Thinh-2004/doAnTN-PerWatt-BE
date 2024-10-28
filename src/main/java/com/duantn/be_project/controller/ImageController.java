package com.duantn.be_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.ImageRepository;
import com.duantn.be_project.model.Image;

import jakarta.servlet.ServletContext;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin("*")
@RestController
public class ImageController {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ServletContext servletContext;

    @GetMapping("imageByProduct/{id}")
    public ResponseEntity<List<Image>> getByIdProduct(@PathVariable("id") Integer id) {
        if(imageRepository.findAllByIdProduct(id) == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageRepository.findAllByIdProduct(id));
    }
    

    @DeleteMapping("image/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id) {
        Image image = imageRepository.findById(id).orElseThrow();
        if (image.getId() == null) {
            return ResponseEntity.notFound().build();
        }
        // Xóa hình ảnh khỏi hệ thống tệp
        Path imagePath = Paths
                .get(servletContext.getRealPath("/files/product-images/" + id + "/" + image.getImagename()));
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            // Xử lý lỗi nếu không thể xóa hình ảnh
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        imageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
