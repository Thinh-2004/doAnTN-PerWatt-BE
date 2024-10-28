package com.duantn.be_project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ForgetPasswordService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendOTP(String toEmail, String otp) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("MÃ OTP LẤY LẠI MẬT KHẨU");

			// Tạo nội dung email với định dạng HTML
			String htmlContent = "<html>" +
					"<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
					"<div style='max-width: 600px; margin: 20px auto; margin-top : 20px; margin-bottom : 20px; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
					+

					// Title and Message
					"<h2 style='text-align: center; color: #333333;'>Bạn đã yêu cầu mã OTP để xác thực</h2>"
					+
					"<p>Xin Chào,</p>" +
					"<p>Bạn nhận được email này vì có yêu cầu quên mật khẩu hoặc đổi mật khẩu. Vui lòng không chia sẻ mã này cho bất kì ai.</p>"
					+
					"<p>Vui lòng nhập mã sau để thực hiện xác minh:</p>" +
					"<div style='text-align: center; margin: 20px 0;'>" +
					"<p style='font-size: 28px; color: #333333; font-weight: bold;'>" + otp + "</p>" +
					"</div>" +

					"<p>Nếu bạn cho rằng đã nhận email này do nhầm lẫn, vui lòng liên hệ với quản trị viên hệ thống của bạn.</p>"
					+

					// Footer Section
					"<div style='text-align: center; margin-top: 30px; padding: 10px; background-color: #f0f0f0; border-radius: 5px;'>"
					+
					"<p style='font-size: 12px; color: #888888;'>Đây là thư được tạo tự động từ PerWatt. Vui lòng không trả lời email này.</p>"
					+
					"</div>" +

					"</div>" +
					"</body>" +
					"</html>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
