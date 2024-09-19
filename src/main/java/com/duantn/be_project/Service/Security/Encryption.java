package com.duantn.be_project.Service.Security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class Encryption {
    private static final String SECRET_KEY = "Thinhtran2482004"; // Khóa bí mật phải thỏa mãn điều kiện 16,24,32 bytes
    private static final String ALGORITHM = "AES"; // Tên thuật toán mã hóa

    // Phương thức mã hóa
    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM); // Tạo khóa bí mật
        Cipher cipher = Cipher.getInstance(ALGORITHM); // Thực hiện mã hóa bằng AES
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);// Khởi tạo chế độ mã hóa
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Phương thức giải mã
    public static String dencrypt(String encrytedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodeData = Base64.getDecoder().decode(encrytedData); // chuyển đổi tham số --> Base64 --> Byte
        byte[] decrypted = cipher.doFinal(decodeData);// Giải mã
        return new String(decrypted);
    }
}
