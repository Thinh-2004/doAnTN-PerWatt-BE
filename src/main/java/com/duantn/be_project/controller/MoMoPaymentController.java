package com.duantn.be_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Service.MoMoService;

import java.security.SecureRandom;

@CrossOrigin("*")
@RestController
public class MoMoPaymentController {
    @Autowired
    private MoMoService moMoService;

    @GetMapping("/pay")
    public ResponseEntity<String> pay(@RequestParam Long amount) {
        String orderId = generateRandomOrderId(20);

        String orderInfo = "Thanh toán bằng MoMo"; // Thông tin đơn hàng
        try {
            String paymentResponse = moMoService.createPayment(amount, orderId, orderInfo);
            return ResponseEntity.ok(paymentResponse); // Trả về URL thanh toán
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Đã xảy ra lỗi trong quá trình xử lý thanh toán: " + e.getMessage());
        }
    }

    // Phương thức tạo ID đơn hàng ngẫu nhiên
    private String generateRandomOrderId(int length) {
        // Chỉ cho phép ký tự chữ cái và số, và thêm dấu gạch dưới hoặc dấu chấm
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
        SecureRandom random = new SecureRandom();
        StringBuilder orderId = new StringBuilder(length);

        // Tạo orderId ngẫu nhiên
        for (int i = 0; i < length; i++) {
            orderId.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Kiểm tra xem orderId có hợp lệ với regex không
        String regex = "^[0-9a-zA-Z]([-_.]*[0-9a-zA-Z]+)*$";
        if (!orderId.toString().matches(regex)) {
            return generateRandomOrderId(length); // Gọi lại phương thức nếu không hợp lệ
        }

        return orderId.toString();
    }

    @PostMapping("/notify")
    public String notifyy() {
        // Xử lý thông báo từ MoMo khi thanh toán hoàn tất
        // Kiểm tra tính hợp lệ của payload, cập nhật trạng thái đơn hàng trong hệ thống
        // Trả về "SUCCESS" để MoMo biết bạn đã nhận được thông báo
        return "SUCCESS";
    }

    @PostMapping("/return")
    public String returnUrl() {
        // Xử lý khi người dùng được chuyển hướng trở lại sau khi thanh toán
        return "Payment completed!";
    }

}
