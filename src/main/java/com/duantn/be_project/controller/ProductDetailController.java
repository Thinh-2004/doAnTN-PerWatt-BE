package com.duantn.be_project.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Service.FirebaseStorageService;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin("*")
@RestController
public class ProductDetailController {
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    FirebaseStorageService firebaseStorageService;

    // MinMax theo danh sách findMore
    @GetMapping("/sidlerMinMax/{name}")
    public ResponseEntity<?> silderMinMax(@PathVariable("name") String name) throws UnsupportedEncodingException {
        String decodeName = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
        List<Object[]> respone = productDetailRepository.minMaxPriceDetail("%" + decodeName + "%");
        return ResponseEntity.ok(respone);
    }

    // GetAll
    @GetMapping("/detailProduct")
    public ResponseEntity<List<ProductDetail>> getAll() {
        return ResponseEntity.ok(productDetailRepository.findAll());
    }

    // GetByIdProduct
    @GetMapping("/detailProduct/{id}")
    public ResponseEntity<List<ProductDetail>> getByIdProduct(@PathVariable("id") Integer id) {
        List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(id);
        if (productDetails == null | productDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDetails);
    }

    // CountDetailProductSoldOutByIdStore
    @GetMapping("/countDetailSoldOut/{id}")
    public ResponseEntity<List<ProductDetail>> CountDetailProductSoldOut(@PathVariable("id") Integer id) {
        List<ProductDetail> productDetails = productDetailRepository.countDetailProductSoldOut(id);
        return ResponseEntity.ok(productDetails);
    }

    // GetByIdProduct
    @GetMapping("/findIdProductByIdProduct/{id}")
    public ResponseEntity<List<ProductDetail>> getIdProductBySlugProduct(@PathVariable("id") Integer id) {
        List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(id);
        if (productDetails == null | productDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDetails);
    }

    // Post
    @PreAuthorize("hasAnyAuthority('Seller')") // Chỉ vai trò là seller mới được gọi
    @PostMapping("/detailProduct")
    public ResponseEntity<?> postDetailProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productDetail") String productDetailJson) {

        ProductDetail productDetail;
        try {
            productDetail = new ObjectMapper().readValue(productDetailJson, ProductDetail.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Lỗi chuyển đổi JSON: " + e.getMessage());
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        Product product = productRepository.findById(productDetail.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productDetail.setProduct(product);

        // Kiểm tra xem sản phẩm chi tiết đã tồn tại chưa
        if (productDetail.getId() != null && productDetailRepository.existsById(productDetail.getId())) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm đã tồn tại.");
        }

        ProductDetail savedProductDetail = productDetailRepository.save(productDetail);

        // Tải ảnh phân loại sản phẩm lên Firebase
        if (file != null && !file.isEmpty()) {
            try {
                // Tải file lên Firebase và nhận URL hoặc tên file
                String imageDetailProduct = firebaseStorageService.uploadToFirebase(file,
                        "productDetails");

                // Cập nhật lại thuộc tính `imagedetail` với tên file Firebase
                savedProductDetail.setImagedetail(imageDetailProduct);
                productDetailRepository.save(savedProductDetail); // Lưu lại chi tiết sản phẩm
            } catch (Exception e) {
                // Xóa chi tiết sản phẩm nếu upload ảnh thất bại
                productDetailRepository.deleteById(savedProductDetail.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi tải ảnh lên Firebase: " + e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("File hình ảnh không hợp lệ.");
        }

        return ResponseEntity.ok(savedProductDetail);
    }

    // Put
    @PreAuthorize("hasAnyAuthority('Seller')") // Chỉ vai trò là seller mới được gọi
    @PutMapping("/detailProduct/{id}")
    public ResponseEntity<?> putDetailProduct(
            @PathVariable("id") Integer id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("productDetail") String productDetailJson) {

        // Lấy thông tin ProductDetail hiện tại từ CSDL
        ProductDetail existingProductDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDetail not found"));

        // Chuyển đổi JSON sang đối tượng ProductDetail
        ProductDetail updatedProductDetail;
        try {
            updatedProductDetail = new ObjectMapper().readValue(productDetailJson, ProductDetail.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Lỗi chuyển đổi JSON: " + e.getMessage());
        }

        // Kiểm tra xem sản phẩm liên quan có tồn tại không
        Product product = productRepository.findById(updatedProductDetail.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        updatedProductDetail.setProduct(product);
        updatedProductDetail.setId(id); // Đảm bảo id được giữ nguyên

        // Lưu tên ảnh cũ
        String oldImageDetail = existingProductDetail.getImagedetail();
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

        // Nếu có tệp ảnh mới, lưu lên Firebase và cập nhật thuộc tính imagedetail
        if (file != null && !file.isEmpty()) {
            try {
                // Tải file lên Firebase và nhận URL hoặc tên file
                String imageDetailProduct = firebaseStorageService.uploadToFirebase(file, "productDetails");
                updatedProductDetail.setImagedetail(imageDetailProduct);

                // Xóa ảnh cũ khỏi Firebase nếu tồn tại
                if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi tải ảnh lên Firebase: " + e.getMessage());
            }
        } else {
            // Nếu không có tệp ảnh mới, giữ nguyên ảnh cũ
            updatedProductDetail.setImagedetail(oldImageDetail);
        }

        // Lưu chi tiết sản phẩm đã cập nhật
        productDetailRepository.save(updatedProductDetail);

        return ResponseEntity.ok(updatedProductDetail);
    }

    // Delete
    @PreAuthorize("hasAnyAuthority('Seller')") // Chỉ vai trò là seller mới được gọi
    @DeleteMapping("detailProduct/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        ProductDetail productDetail = productDetailRepository.findById(id).orElse(null);
        if (productDetail == null || productDetail.getId() == null) {// Nếu id không được tìm thấy
            return ResponseEntity.notFound().build(); // Trả lỗi 404
        }
        // Xóa hình ảnh khỏi Firebase Storage
        try {
            // Giải mã URL trước
            String decodedUrl = java.net.URLDecoder.decode(productDetail.getImagedetail(),
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
        productDetailRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
