package com.duantn.be_project.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private static final String BUCKET_NAME = "image-perwatt.firebasestorage.app";

    // Hàm lưu file lên Firebase Storage
    public String uploadToFirebase(MultipartFile file, String path) throws IOException {
        // Kiểm tra file có hợp lệ không
        if (file.isEmpty()) {
            throw new IOException("File không hợp lệ hoặc trống.");
        }

        // Lấy bucket Firebase từ StorageClient và chỉ định tên bucket
        Bucket bucket = StorageClient.getInstance().bucket(BUCKET_NAME);

        // Tạo đường dẫn file trong bucket với UUID để tránh trùng tên
        String fileName = path + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Lưu file lên Firebase
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        // Trả về URL công khai của file, theo chuẩn URL Firebase Storage
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8.toString()));
    }

    // Hàm xử lí upload hình ảnh theo gender user
    public String uploadToFirebaseByUserGender(byte[] content, String filePath) {
        try {
            // Lấy bucket mặc định từ StorageClient
            Bucket bucket = StorageClient.getInstance().bucket(BUCKET_NAME);

            if (bucket == null) {
                throw new IllegalStateException("Bucket không được cấu hình đúng!");
            }

            // Tạo file trên Firebase Storage
            Blob blob = bucket.create(filePath, content, "image/jpeg");

            // Trả về URL công khai
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Xóa file từ Firebase Storage
    public void deleteFileFromFirebase(String filePath) {
        try {
            // Lấy bucket Firebase
            Bucket bucket = StorageClient.getInstance().bucket(BUCKET_NAME);

            // Lấy reference đến file trong bucket
            Blob blob = bucket.get(filePath);

            // Kiểm tra nếu file tồn tại và xóa
            if (blob != null) {
                blob.delete();
                System.out.println("File đã được xóa thành công: " + filePath);
            } else {
                System.out.println("Không tìm thấy file: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xóa file: " + e.getMessage());
        }
    }

    // Cập nhật file (tải lên lại file mới và xóa file cũ)
    public String updateFileInFirebase(MultipartFile newFile, String oldFilePath, String path) throws IOException {
        // Xóa file cũ nếu có
        deleteFileFromFirebase(oldFilePath);

        // Tải lên file mới và lấy URL
        return uploadToFirebase(newFile, path);
    }
}
