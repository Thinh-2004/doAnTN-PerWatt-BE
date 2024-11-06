package com.duantn.be_project.controller;

import com.duantn.be_project.Repository.BannerRepository;
import com.duantn.be_project.model.Banner;
import com.duantn.be_project.model.User;
import com.duantn.be_project.untils.UploadImages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.duantn.be_project.Repository.UserRepository;

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
    private UploadImages uploadImages;

    // Get all banners
    @GetMapping
    public ResponseEntity<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        logger.info("Fetched all banners successfully.");
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
                String imgUrl = uploadImages.saveBannerImage(image, banner.getUser().getId());
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
            banner.setId(id);
            Integer userId = banner.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            banner.setUser(user);
        } catch (Exception e) {
            logger.error("Failed to parse banner JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            try {
                String imgUrl = uploadImages.saveBannerImage(image, banner.getUser().getId());
                banner.setImg(imgUrl);
            } catch (Exception e) {
                logger.error("Failed to save banner image: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        } else {
            // Lấy ảnh cũ từ DB nếu không có ảnh mới
            Banner existingBanner = bannerRepository.findById(id).get();
            banner.setImg(existingBanner.getImg());
        }

        Banner updatedBanner = bannerRepository.save(banner);
        logger.info("Banner with ID {} updated successfully.", id);
        return ResponseEntity.ok(updatedBanner);
    }

    // Delete a banner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Integer id) {
        if (!bannerRepository.existsById(id)) {
            logger.warn("Failed to delete banner: Banner with ID {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        bannerRepository.deleteById(id);
        logger.info("Banner with ID {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}
