package com.duantn.be_project.untils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadImages {
    private static final String UPLOAD_DIR = "src/main/resources/static/files/";

    private Path getPath(String folder, String filename) {
        Path dir = Paths.get(UPLOAD_DIR, folder);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory!", e);
            }
        }
        return dir.resolve(filename);
    }

    public byte[] read(String folder, String filename) {
        Path path = this.getPath(folder, filename);
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(MultipartFile file, String folder) {
        String name = System.currentTimeMillis() + file.getOriginalFilename();
        String filename = Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
        Path path = this.getPath(folder, filename);
        try {
            file.transferTo(path);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(String folder, String filename) {
        Path path = this.getPath(folder, filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> list(String folder) {
        List<String> filenames = new ArrayList<>();
        Path dir = Paths.get(UPLOAD_DIR, folder);
        if (Files.exists(dir)) {
            try {
                Files.list(dir).forEach(file -> {
                    if (Files.isRegularFile(file)) {
                        filenames.add(file.getFileName().toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filenames;
    }

    // Save user image
    public String saveUserImage(MultipartFile file, Integer userId) {
        return save(file, "user/" + userId);
    }

    // Save store image
    public String saveStoreImage(MultipartFile file, Integer storeId) {
        return save(file, "store/" + storeId);
    }

    public String saveUserImageBasedOnGender(boolean isMale, Integer userId) {
        // Đặt tên file dựa trên giới tính
        String defaultAvatar = isMale ? "nam.jpg" : "nu.jpg";

        // Tạo đường dẫn tới file mặc định
        Path sourcePath = Paths.get("src/main/resources/static/files/images", defaultAvatar);

        // Đường dẫn tới thư mục người dùng
        Path targetDir = Paths.get(UPLOAD_DIR, "user", String.valueOf(userId));
        Path targetPath = targetDir.resolve(defaultAvatar); //Nối đường dẫn

        try {
            // Tạo thư mục nếu chưa tồn tại
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Sao chép file từ nguồn tới đích
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return defaultAvatar;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // // Save product image
    // public String saveProductImage(MultipartFile file, Integer productId) {
    // return save(file, "product/" + productId);
    // }
}
