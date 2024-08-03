package com.duantn.be_project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.be_project.model.User;
import com.duantn.be_project.repository.ForgetPasswordRepository;

@Controller
@RequestMapping("/api")
public class ChangePasswordController {
	@Autowired
	private ForgetPasswordRepository forgetPasswordRepository;

	@PostMapping("/changePassword")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
		String currentPassword = request.get("currentPassword");
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
			if (user != null && user.getPassword().equals(currentPassword)) { // Kiểm tra mật khẩu hiện tại
				user.setPassword(newPassword); // Đặt mật khẩu mới cho người dùng
				forgetPasswordRepository.save(user);
				return ResponseEntity.ok("Đổi mật khẩu thành công!");
			}
			return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng!");
		}

		return ResponseEntity.badRequest().body("Có lỗi xảy ra. Vui lòng thử lại.");
	}

}
