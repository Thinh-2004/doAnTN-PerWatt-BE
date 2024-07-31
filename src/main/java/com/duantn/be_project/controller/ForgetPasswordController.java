package com.duantn.be_project.controller;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.be_project.model.User;
import com.duantn.be_project.repository.ForgetPasswordRepository;
import com.duantn.be_project.service.ForgetPasswordService;

@Controller
@RequestMapping("/api")
public class ForgetPasswordController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendForgotPasswordEmail(@RequestBody Map<String, String> request) {
        String toEmail = request.get("toEmail");

        if (toEmail == null || toEmail.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được bỏ trống!"));
        } else if (!toEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không đúng định dạng!"));
        }

        User user = forgetPasswordRepository.findByEmail(toEmail);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không tồn tại. Vui lòng kiểm tra lại."));
        }

        String otp = OTPUtil.generateOTP(6); // Tạo OTP có độ dài 6 ký tự
        try {
            forgetPasswordService.sendOTP(toEmail, otp);
            return ResponseEntity.ok(Map.of("message", "OTP đã được gửi tới email của bạn.", "otp", otp, "email", toEmail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Đã xảy ra lỗi khi gửi OTP."));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestBody Map<String, String> request) {
        String otp = request.get("otp");
        String generatedOTP = request.get("generatedOTP");

        if (generatedOTP != null && generatedOTP.equals(otp)) {
            return ResponseEntity.ok("Xác nhận thành công!");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không đúng!");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        String email = request.get("email");

        if (newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống mật khẩu mới!");
        } else if (confirmPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Không được bỏ trống xác nhận mật khẩu!");
        } else if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("Mật khẩu không được nhỏ hơn 8 kí tự!");
        } else if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Mật khẩu và xác nhận mật khẩu không khớp!");
        }

        if (email != null) {
            User user = forgetPasswordRepository.findByEmail(email);
            if (user != null) {
                user.setPassword(newPassword); // Đặt mật khẩu mới cho người dùng
                forgetPasswordRepository.save(user);
                return ResponseEntity.ok("Đổi mật khẩu thành công!");
            }
        }

        return ResponseEntity.badRequest().body("Có lỗi xảy ra. Vui lòng thử lại.");
    }

    public static class OTPUtil {
        private static final String CHARACTERS = "0123456789";
        private static final SecureRandom RANDOM = new SecureRandom();
        
        public static String generateOTP(int length) {
            StringBuilder otp = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                otp.append(CHARACTERS.charAt(index));
            }
            return otp.toString();
        }
    }
}
