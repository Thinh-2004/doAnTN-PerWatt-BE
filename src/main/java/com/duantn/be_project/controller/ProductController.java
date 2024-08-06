package com.duantn.be_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import com.duantn.be_project.Repository.ImageRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.model.Image;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.untils.FileManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@CrossOrigin("*")
@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    FileManagerService fileManagerService;
    @Autowired
    ServletContext servletContext;

    // GetAll
    @GetMapping("/pageHome")
    public ResponseEntity<List<Product>> getAll(Model model) {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // GetAllByIdStore
    @GetMapping("/productStore/{id}")
    public ResponseEntity<List<Product>> getAllProductByIdUser(@PathVariable("id") Integer storeId) {
        List<Product> products = productRepository.findAllByStoreIdWithImages(storeId);
        if (products == null || products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Log để kiểm tra dữ liệu
        products.forEach(product -> {
            System.out.println("Product ID: " + product.getId());
            product.getImages().forEach(image -> System.out
                    .println("Image ID: " + image.getId() + ", Image Name: " + image.getImagename()));
        });
        return ResponseEntity.ok(products);
    }

    // GetByIdProduct
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getByIdProduct(@PathVariable("id") Integer id) {
        Product product = productRepository.findById(id).orElseThrow();
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // // Post Store Product
    // @PostMapping("/productCreate")
    // public ResponseEntity<Product> post(@RequestBody Product product) {
    // // TODO: process POST request
    // if (productRepository.existsById(product.getId())) {
    // return ResponseEntity.badRequest().build();
    // }
    // return ResponseEntity.ok(productRepository.save(product));
    // }

    // Post Store Product
    @PostMapping("/productCreate")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("files") MultipartFile[] files) {

        System.out.println("Received product: " + productJson);

        // Tạo nơi chứa Json
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;
        try {
            product = objectMapper.readValue(productJson, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid product data: " + e.getMessage());
        }

        System.out.println("Parsed product: " + product);

        // Lưu  product
        Product savedProduct;
        try {
            savedProduct = productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save product: " + e.getMessage());
        }

        System.out.println("Saved product: " + savedProduct);

        // Lưu files và update product với image 
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            System.out.println("Received file: " + file.getOriginalFilename());

            // Lưu file và get URL của filename
            String imageUrl = fileManagerService.save(file, savedProduct.getId());

            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            }
        }

        // Tạo Image entities 
        List<Image> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setImagename(imageUrl);
            image.setProduct(savedProduct);
            images.add(image);
        }

        // Lưu images
        try {
            imageRepository.saveAll(images); // Ensure you have a repository to save images
            savedProduct.setImages(images); // Update product with image entities
            productRepository.save(savedProduct); // Save updated product
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update product with images: " + e.getMessage());
        }

        return ResponseEntity.ok("Product created successfully with images");
    }

    // Put Store Product
    @PutMapping("/productUpdate/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Integer id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        // Kiểm tra xem sản phẩm có tồn tại không
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển đổi chuỗi JSON thành đối tượng Product
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;
        try {
            product = objectMapper.readValue(productJson, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Dữ liệu sản phẩm không hợp lệ: " + e.getMessage());
        }

        // Đảm bảo ID của sản phẩm khớp với biến đường dẫn
        product.setId(id);

        // Lưu sản phẩm đã cập nhật
        Product updatedProduct;
        try {
            updatedProduct = productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể cập nhật sản phẩm: " + e.getMessage());
        }

        // Xử lý các tệp mới nếu có
        if (files != null && files.length > 0) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                System.out.println("Đã nhận tệp: " + file.getOriginalFilename());

                // Lưu tệp và lấy URL hoặc tên tệp
                String imageUrl = fileManagerService.save(file, updatedProduct.getId());

                if (imageUrl != null) {
                    imageUrls.add(imageUrl);
                }
            }

            // Tạo các đối tượng Image cho từng URL hình ảnh mới và liên kết với sản phẩm
            List<Image> images = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImagename(imageUrl);
                image.setProduct(updatedProduct);
                images.add(image);
            }

            // Lưu các hình ảnh mới và cập nhật sản phẩm với các đối tượng hình ảnh
            try {
                imageRepository.saveAll(images); // Đảm bảo bạn có repository để lưu hình ảnh
                updatedProduct.getImages().addAll(images); // Thêm hình ảnh mới vào những hình ảnh hiện có
                productRepository.save(updatedProduct); // Lưu sản phẩm đã cập nhật
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể cập nhật sản phẩm với hình ảnh mới: " + e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }

    // Delete
    @DeleteMapping("/ProductDelete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // Kiểm tra xem sản phẩm có tồn tại không
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Lấy thông tin sản phẩm để lấy danh sách hình ảnh
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Xóa hình ảnh từ cơ sở dữ liệu
            List<Image> images = product.getImages();
            for (Image image : images) {
                imageRepository.delete(image); // Xóa hình ảnh khỏi cơ sở dữ liệu

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
            }
        }

        // Xóa sản phẩm khỏi cơ sở dữ liệu
        productRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    // Tìm idStore
    @GetMapping("/searchStore/{id}")
    public ResponseEntity<Store> getIdStoreByIdUser(@PathVariable("id") Integer idUser) {
        Store store = storeRepository.findStoreByIdUser(idUser);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }

}
