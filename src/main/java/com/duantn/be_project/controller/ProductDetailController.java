package com.duantn.be_project.controller;

import java.io.File;
<<<<<<< HEAD
=======
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
>>>>>>> a6abd943928eae065c0e9d81e347ca6ca254abf4
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
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

        // Kiểm tra xem sản phẩm đã tồn tại chưa
        if (productDetail.getId() != null && productDetailRepository.existsById(productDetail.getId())) {
            return ResponseEntity.badRequest().body("Sản phẩm đã tồn tại.");
        }

        // Lưu sản phẩm chi tiết
        ProductDetail savedProductDetail = productDetailRepository.save(productDetail);

        // Lưu ảnh phân loại sản phẩm
        String imageDetailProduct = uploadImages.saveDetailProductImage(file, savedProductDetail.getId());

        // Cập nhật lại thuộc tính imagedetail
        savedProductDetail.setImagedetail(imageDetailProduct);
        productDetailRepository.save(savedProductDetail);

        return ResponseEntity.ok(savedProductDetail);
    }

    // Put
    @PutMapping("/detailProduct/{id}")
    public ResponseEntity<?> putDetailProduct(
            @PathVariable("id") Integer id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("productDetail") String productDetailJson) {

        ProductDetail existingProductDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDetail not found"));

        ProductDetail updatedProductDetail;
        try {
            updatedProductDetail = new ObjectMapper().readValue(productDetailJson, ProductDetail.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Lỗi chuyển đổi JSON: " + e.getMessage());
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        Product product = productRepository.findById(updatedProductDetail.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        updatedProductDetail.setProduct(product);
        updatedProductDetail.setId(id); // Đảm bảo id được giữ nguyên

        // Lưu tên ảnh cũ
        String oldImageDetail = existingProductDetail.getImagedetail();

        // Nếu có tệp ảnh mới, lưu ảnh mới và cập nhật thuộc tính imagedetail
        if (file != null && !file.isEmpty()) {
            String imageDetailProduct = uploadImages.saveDetailProductImage(file, id);
            updatedProductDetail.setImagedetail(imageDetailProduct);

            // Xóa ảnh cũ sau khi cập nhật thành công ảnh mới
            if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                String filePath = String.format("src/main/resources/static/files/detailProduct/%d/%s", id,
                        oldImageDetail);
                File fileOld = new File(filePath);
                if (fileOld.exists()) {// Nếu file tồn tại
                    fileOld.delete();
                } else {
                    System.out.println("Không xóa được ảnh");
                }
            } else {
                System.out.println("Ảnh cũ không tồn tại");
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
    @DeleteMapping("detailProduct/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!productDetailRepository.existsById(id)) {// Nếu id không được tìm thấy
            return ResponseEntity.notFound().build(); // Trả lỗi 404
        }
        productDetailRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
