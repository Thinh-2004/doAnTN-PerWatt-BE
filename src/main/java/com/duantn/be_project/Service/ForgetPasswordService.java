package com.duantn.be_project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ForgetPasswordService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendOTP(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Mã OTP lấy lại mật khẩu");
		message.setText("Bạn đang yêu cầu khôi phục/thay đổi Mật khẩu cho tài khoản. Mã OTP: " + otp
				+ ". Vui lòng không cung cấp mã OTP cho bất kì ai để bảo vệ tài khoản!");
		mailSender.send(message);
	}	
}
