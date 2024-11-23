package com.duantn.be_project.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    // // Hàm lưu file lên Firebase Storage
    // public String uploadToFirebase(MultipartFile file, String path) throws
    // IOException {
    // // Lấy bucket Firebase
    // Bucket bucket = StorageClient.getInstance().bucket();

    // // Tạo đường dẫn file trong bucket
    // String fileName = path + "/" + file.getOriginalFilename();

    // // Lưu file lên Firebase
    // Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

    // // Trả về URL công khai của file
    // return String.format("https://storage.googleapis.com/%s/%s",
    // bucket.getName(), blob.getName());
    // }

    public String uploadToFirebase(MultipartFile file, String path) throws IOException {
        // Lấy bucket Firebase
        Bucket bucket = StorageClient.getInstance().bucket();

        // Tạo đường dẫn file trong bucket
        String fileName = path + "/" + file.getOriginalFilename();

        // Lưu file lên Firebase
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        // Trả về URL công khai của file, theo chuẩn URL Firebase Storage
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8.toString()));
    }

    // Xóa file từ Firebase Storage
    public void deleteFileFromFirebase(String filePath) {
        try {
            // Lấy bucket Firebase
            Bucket bucket = StorageClient.getInstance().bucket();

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
