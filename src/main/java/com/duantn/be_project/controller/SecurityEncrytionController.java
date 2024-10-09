package com.duantn.be_project.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Service.Security.Encryption;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin("*")
@RestController
public class SecurityEncrytionController {
    @Autowired
    Encryption encryption;

    @PostMapping("/Encryption")
    public ResponseEntity<String> dencryption(@RequestBody Map<String, String> requestBody) {
        try {
            String user = requestBody.get("user");
            System.out.println("Received base64 string: " + user); // In ra giá trị nhận được

            // Giải mã base64
            byte[] decodedBytes = Base64.getDecoder().decode(user);
            String dencryptionFormat = new String(decodedBytes, "UTF-8");

            // Thực hiện giải mã dữ liệu
            String dencryptionData = encryption.dencrypt(dencryptionFormat);

            return ResponseEntity.ok(dencryptionData); // Trả về dữ liệu đã giải mã
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dữ liệu không phải base64 hợp lệ");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi giải mã dữ liệu");
        }
    }

}
