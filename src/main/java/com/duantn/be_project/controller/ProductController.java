package com.duantn.be_project.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.be_project.Repository.ImageRepository;
import com.duantn.be_project.Repository.OrderRepository;
import com.duantn.be_project.Repository.ProductDetailRepository;
import com.duantn.be_project.Repository.ProductRepository;
import com.duantn.be_project.Repository.StoreRepository;
import com.duantn.be_project.Service.SlugText.SlugText;
import com.duantn.be_project.model.Image;
import com.duantn.be_project.model.Product;
import com.duantn.be_project.model.ProductDetail;
import com.duantn.be_project.model.Store;
import com.duantn.be_project.untils.FileManagerService;
import com.duantn.be_project.untils.UploadImages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;

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
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    UploadImages uploadImages;
    @Autowired
    SlugText slugText;

    // GetAll
    @GetMapping("/pageHome")
    public ResponseEntity<List<Product>> getAll(Model model) {
        return ResponseEntity.ok(productRepository.findAllDesc());
    }

    @GetMapping("/findMore/{name}")
    public ResponseEntity<List<Product>> findMore(@PathVariable("name") String name) throws UnsupportedEncodingException {
        String decodeName = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
        List<Product> product = productRepository.findMoreProductByNameCateOrTrademark(decodeName);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(product);
    }

    // GetAllByIdStore
    @GetMapping("/productStore/{slug}")
    public ResponseEntity<List<Product>> getStoreBySlugStore(@PathVariable("slug") String slug) {
        List<Product> products = productRepository.findAllByStoreIdWithSlugStore(slug);
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

    // GetAllByIdStore
    @GetMapping("/countBySlugProduct/{id}")
    public ResponseEntity<List<Product>> getAllProductByIdStore(@PathVariable("id") Integer id) {
        List<Product> products = productRepository.CountProductByIdStore(id);
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
    // @GetMapping("/product/{id}")
    // public ResponseEntity<Product> getByIdProduct(@PathVariable("id") Integer id)
    // {
    // Product product = productRepository.findById(id).orElseThrow();
    // if (product == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(product);
    // }

    @GetMapping("/product/{slug}")
    public ResponseEntity<Product> getBySlugNameProduct(@PathVariable("slug") String slug) {
        return productRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productCreate")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("productDetails") String productDetailsJson,
            @RequestPart("files") MultipartFile[] files) throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;
        List<ProductDetail> productDetails;

        // Chuyển đổi JSON thành đối tượng Product
        product = objectMapper.readValue(productJson, Product.class);

        // Gán tên sản phẩm cho slug
        product.setSlug(slugText.generateUniqueSlug(product.getName()));

        // Lưu Product trước và lấy productId
        Product savedProduct = productRepository.save(product);
        try {
            // Chuyển đổi JSON thành danh sách ProductDetail
            TypeReference<List<ProductDetail>> typeRef = new TypeReference<List<ProductDetail>>() {
            };
            productDetails = objectMapper.readValue(productDetailsJson, typeRef);

            // Duyệt qua từng ProductDetail để lưu vào cơ sở dữ liệu trước để lấy id
            for (ProductDetail detail : productDetails) {
                detail.setProduct(savedProduct); // Gán Product đã lưu vào ProductDetail
                ProductDetail savedDetail = productDetailRepository.save(detail); // Lưu tạm thời ProductDetail để lấy
                                                                                  // id

                try {
                    if (detail.getImagedetail() != null && !detail.getImagedetail().isEmpty()) {
                        // Chuyển đổi chuỗi base64 thành MultipartFile
                        MultipartFile imageDetail = uploadImages.base64ToMultipartFile(detail.getImagedetail());
                        // Lưu hình ảnh lên server và lấy đường dẫn lưu
                        String imageDetailPath = uploadImages.saveDetailProductImage(imageDetail, savedDetail.getId());
                        savedDetail.setImagedetail(imageDetailPath); // Cập nhật đường dẫn thực tế cho imagedetail
                    } else {
                        savedDetail.setImagedetail(null); // Cập nhật đường dẫn thực tế cho imagedetail

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // Xử lý lỗi khi chuyển đổi base64 thành MultipartFile
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to process image for ProductDetail: " + e.getMessage());
                }

                // Lưu lại ProductDetail sau khi đã cập nhật imagedetail
                productDetailRepository.save(savedDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }

        System.out.println("Saved product and details: " + product);

        // Lưu các ảnh trong files vào server và liên kết với Product
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            System.out.println("Received file: " + file.getOriginalFilename());

            // Lưu file và lấy URL
            String imageUrl = fileManagerService.save(file, savedProduct.getId());

            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            }
        }

        // Tạo các đối tượng Image và liên kết với Product
        List<Image> images = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setImagename(imageUrl);
            image.setProduct(savedProduct);
            images.add(image);
        }

        // Lưu danh sách Image vào cơ sở dữ liệu
        try {
            imageRepository.saveAll(images);
            savedProduct.setImages(images);
            productRepository.save(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update product with images: " + e.getMessage());
        }

        return ResponseEntity.ok("Product created successfully with images and details");
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
            if (!product.getSlug().isEmpty() || product.getSlug() != null) {
                product.setSlug(slugText.generateUniqueSlug(product.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Dữ liệu sản phẩm không hợp lệ: " + e.getMessage());
        }

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
                // Xóa hình ảnh khỏi hệ thống tệp
                try {
                    uploadImages.deleteFolderAndFile(
                            Paths.get("src/main/resources/static/files/product-images/" + id).toString());
                } catch (Exception e) {
                    // Xử lý lỗi nếu không thể xóa hình ảnh
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                imageRepository.delete(image); // Xóa hình ảnh khỏi cơ sở dữ liệu
            }

            // Xóa detailProduct từ cơ sở dữ liệu
            List<ProductDetail> productDetails = productDetailRepository.findByIdProduct(id);
            for (ProductDetail detail : productDetails) {
                // Xóa hình ảnh khỏi hệ thống tệp
                try {
                    uploadImages.deleteFolderAndFile(
                            Paths.get("src/main/resources/static/files/detailProduct/" + detail.getId()).toString());
                } catch (Exception e) {
                    // Xử lý lỗi nếu không thể xóa hình ảnh
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                productDetailRepository.delete(detail); // Xóa chi tiết sản phẩm khỏi cơ sở dữ liệu
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
        // if (store == null) {
        // return ResponseEntity.notFound().build();
        // }
        return ResponseEntity.ok(store);
    }

    // CountOrderBuy
    @GetMapping("/countOrderSuccess/{id}")
    public ResponseEntity<Integer> countOrderBuyed(@PathVariable("id") Integer idProductDetail) {
        Integer countOrder = orderRepository.countOrderBuyed(idProductDetail);
        return ResponseEntity.ok(countOrder);
    }

}
