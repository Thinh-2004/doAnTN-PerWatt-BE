package com.duantn.be_project.untils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
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

    public String saveDetailProductImage(MultipartFile file, Integer DetailProductId) {
        return save(file, "detailProduct/" + DetailProductId);
    }

    public String saveUserImageBasedOnGender(boolean isMale, Integer userId) {
        // Đặt tên file dựa trên giới tính
        String defaultAvatar = isMale ? "nam.jpg" : "nu.jpg";

        // Tạo đường dẫn tới file mặc định
        Path sourcePath = Paths.get("src/main/resources/static/files/images", defaultAvatar);

        // Đường dẫn tới thư mục người dùng
        Path targetDir = Paths.get(UPLOAD_DIR, "user", String.valueOf(userId));
        Path targetPath = targetDir.resolve(defaultAvatar); // Nối đường dẫn

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

    // Hàm tải ảnh từ URL và lưu vào server
    public String saveImageUserByLoginGoogle(String imageUrl, Integer userId) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();

            // Cắt chuỗi URL để lấy phần cần thiết
            String[] parts = imageUrl.split("/");

            // Kiểm tra độ dài của URL
            if (parts.length >= 5) {
                // Tách phần thứ 5 theo dấu '=' để lấy chuỗi trước dấu '='
                String[] subParts = parts[4].split("=");
                String fileNamePart = subParts[0]; // Phần tên file cần lấy

                // Đường dẫn đến nơi lưu ảnh (thay đổi đường dẫn theo nhu cầu của bạn)
                String folderPath = UPLOAD_DIR + "user/" + userId;
                Path folder = Paths.get(folderPath);

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }

                // Tạo tên file với định dạng JPG
                String filename = fileNamePart + ".jpg";
                Path filePath = Paths.get(folderPath, filename);

                // Lưu ảnh vào server
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                // Đóng kết nối và input stream
                inputStream.close();
                connection.disconnect();

                // Trả về tên file đã lưu
                return filename;
            } else {
                return null; // URL không đúng định dạng
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null; // Lỗi khi tải hoặc lưu file
        }
    }

    public void deleteFolderAndFile(String folderPath) {
        Path folder = Paths.get(folderPath);
        try {
            // Kiểm tra xem thư mục có tồn tại không
            if (Files.exists(folder) && Files.isDirectory(folder)) {
                // Xóa tất cả các tệp trong thư mục
                Files.walk(folder)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (file.isFile()) {
                                boolean deleted = file.delete();
                                if (deleted) {
                                    System.out.println("Deleted file: " + file.getPath());
                                } else {
                                    System.out.println("Failed to delete file: " + file.getPath());
                                }
                            }
                        });
                // Xóa tất cả các thư mục con
                Files.walk(folder)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (file.isDirectory()) {
                                boolean deleted = file.delete();
                                if (deleted) {
                                    System.out.println("Deleted directory: " + file.getPath());
                                } else {
                                    System.out.println("Failed to delete directory: " + file.getPath());
                                }
                            }
                        });
            } else {
                System.out.println("Folder does not exist or is not a directory: " + folderPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // // Save product image
    // public String saveProductImage(MultipartFile file, Integer productId) {
    // return save(file, "product/" + productId);
    // }
    public MultipartFile base64ToMultipartFile(String base64Image) throws IOException {
        // Tách phần dữ liệu base64
        String[] parts = base64Image.split(",");
        String imageData = parts[1]; // Phần dữ liệu base64

        byte[] imageBytes = Base64.getDecoder().decode(imageData);

        // Tạo MultipartFile từ dữ liệu
        return new Base64MultipartFile(imageBytes, "image.png", "image/png");
    }

    public String saveBannerImage(MultipartFile file, Integer bannerId) {
        return save(file, "banner/" + bannerId);
    }

}
