package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.BannerRepository;
import com.duantn.be_project.model.Banner;
import com.duantn.be_project.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.Service.FirebaseStorageService;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/banners")
public class BannerController {

    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    FirebaseStorageService firebaseStorageService;

    // Get all banners
    @GetMapping
    public ResponseEntity<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        logger.info("Fetched all banners successfully.");
        return ResponseEntity.ok(banners);
    }

    @PreAuthorize("hasAnyAuthority('Admin_All_Function','Admin_Manage_Banner')")
    @GetMapping("list")
    public ResponseEntity<List<Banner>> getAllBannersForAmin() {
        List<Banner> banners = bannerRepository.findAll();
        logger.info("Fetched all banners successfully.");
        return ResponseEntity.ok(banners);
    }

    @GetMapping("checkShowBannerMid")
    public ResponseEntity<?> checkShowBannerMid() {
        List<Banner> banners = bannerRepository.findBannerByParameter();
        return ResponseEntity.ok(banners);
    }

    // Get banner by ID
    @GetMapping("/{id}")
    public ResponseEntity<Banner> getBannerById(@PathVariable Integer id) {
        Optional<Banner> banner = bannerRepository.findById(id);
        if (banner.isPresent()) {
            logger.info("Banner with ID {} fetched successfully.", id);
            return ResponseEntity.ok(banner.get());
        } else {
            logger.warn("Banner with ID {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Create a new banner
    @PreAuthorize("hasAnyAuthority('Admin_All_Function','Admin_Manage_Banner')")
    @PostMapping
    public ResponseEntity<Banner> createBanner(
            @RequestParam("banner") String bannerJson,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Banner banner;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            banner = objectMapper.readValue(bannerJson, Banner.class);
            Integer userId = banner.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            banner.setUser(user);
        } catch (Exception e) {
            logger.error("Failed to parse banner JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imgUrl = firebaseStorageService.uploadToFirebase(image, "banners");
                banner.setImg(imgUrl);
            } catch (Exception e) {
                logger.error("Failed to save banner image: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        Banner savedBanner = bannerRepository.save(banner);
        logger.info("Banner created successfully with ID {}.", savedBanner.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBanner);
    }

    // Update an existing banner
    @PreAuthorize("hasAnyAuthority('Admin_All_Function','Admin_Manage_Banner')")
    @PutMapping("/{id}")
    public ResponseEntity<Banner> updateBanner(
            @PathVariable Integer id,
            @RequestParam("banner") String bannerJson,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        if (!bannerRepository.existsById(id)) {
            logger.warn("Failed to update banner: Banner with ID {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Banner banner;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            banner = objectMapper.readValue(bannerJson, Banner.class);

            Integer userId = banner.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            banner.setUser(user);
        } catch (Exception e) {
            logger.error("Failed to parse banner JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }

        banner.setId(id);
        Banner existingBanner = bannerRepository.findById(banner.getId()).orElseThrow();
        // Lưu tên ảnh cũ
        String oldImageDetail = existingBanner.getImg();
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
        // Tải hình ảnh lên firebase
        if (image != null && !image.isEmpty()) {
            try {
                String imgUrl = firebaseStorageService.uploadToFirebase(image, "banners");
                // Xóa ảnh cũ trên Firebase nếu có
                if (oldImageDetail != null && !oldImageDetail.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFileFromFirebase(filePath);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ trên Firebase: " + e.getMessage());
                    }
                }
                banner.setImg(imgUrl);
            } catch (Exception e) {
                logger.error("Failed to save banner image: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        } else {
            // Lấy ảnh cũ từ DB nếu không có ảnh mới
            banner.setImg(existingBanner.getImg());
        }

        Banner updatedBanner = bannerRepository.save(banner);
        logger.info("Banner with ID {} updated successfully.", id);
        return ResponseEntity.ok(updatedBanner);
    }

    // Delete a banner
    @PreAuthorize("hasAnyAuthority('Admin_All_Function','Admin_Manage_Banner')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Integer id) {
        Banner banner = bannerRepository.findById(id).orElseThrow();
        if (banner == null || banner.getId() == null) {
            logger.warn("Failed to delete banner: Banner with ID {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Xóa hình ảnh khỏi Firebase Storage
        try {
            // Giải mã URL trước
            String decodedUrl = java.net.URLDecoder.decode(banner.getImg(),
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
        try {
            bannerRepository.deleteById(id);
            logger.info("Banner with ID {} deleted successfully.", id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể xóa hình ảnh khỏi cơ sở dữ liệu: " + e.getMessage());
        }
        // Trả về thành công nếu mọi thứ đều ổn
        return ResponseEntity.ok().build();
    }
}
