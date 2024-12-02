package com.duantn.be_project.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Kiểm tra xem FirebaseApp đã được khởi tạo chưa
            if (FirebaseApp.getApps().isEmpty()) {
                // Sử dụng InputStream để đọc tệp từ resources
                InputStream serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream("firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new IOException("Tệp firebase-service-account.json không tìm thấy.");
                }

                // Cấu hình Firebase với thông tin xác thực
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                // Khởi tạo FirebaseApp nếu chưa được khởi tạo
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ nếu có lỗi khi đọc tệp JSON hoặc cấu hình Firebase
        }
    }
}
