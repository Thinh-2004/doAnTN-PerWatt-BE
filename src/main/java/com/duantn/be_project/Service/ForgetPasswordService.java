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

	public void sendMailCancelVNPay(String toEmail, String fullName, String nameStore, String note, String VNPay) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String str = VNPay;
			String modifiedStr = str.substring(0, 1).toLowerCase() + str.substring(1);
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng đơn hàng của anh/chị đã bị huỷ bởi người bán. Lý do huỷ đơn hàng là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã <strong>" + modifiedStr
					+ "</strong>, chúng tôi mong anh/chị vui lòng cung cấp thông tin cần thiết để chúng tôi có thể tiến hành hoàn tiền cho anh/chị.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailCancelMoMo(String toEmail, String fullName, String nameStore, String note, String Momo) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String str = Momo;
			String modifiedStr = str.substring(0, 1).toLowerCase() + str.substring(1);
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng đơn hàng của anh/chị đã bị huỷ bởi người bán. Lý do huỷ đơn hàng là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã <strong>" + modifiedStr
					+ "</strong>, chúng tôi mong anh/chị vui lòng cung cấp thông tin cần thiết để chúng tôi có thể tiến hành hoàn tiền cho anh/chị.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailCancelCod(String toEmail, String fullName, String nameStore, String note, String VNPay) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String str = VNPay;
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng đơn hàng của anh/chị đã bị huỷ bởi người bán. Lý do huỷ đơn hàng là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã chọn phương thức thanh toán khi nhận hàng, việc huỷ đơn hàng này sẽ không ảnh hưởng đến quá trình thanh toán. Anh/chị vui lòng không cần phải thực hiện bất kỳ bước thanh toán nào cho đơn hàng này.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailCancelTruck(String toEmail, String fullName, String nameStore, String note, String Momo) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng đơn hàng của anh/chị đã bị huỷ bởi người bán. Lý do huỷ đơn hàng là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã chọn phương thức thanh toán khi nhận hàng, việc huỷ đơn hàng này sẽ không ảnh hưởng đến quá trình thanh toán. Anh/chị vui lòng không cần phải thực hiện bất kỳ bước thanh toán nào cho đơn hàng này.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailRefun(String toEmail, String fullName, String nameStore, String note, String orderStatus) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng yêu cầu trả hàng của anh/chị đã bị từ chối bởi người bán. Lý do từ chối là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã chọn phương thức thanh toán khi nhận hàng, việc trả hàng này sẽ không ảnh hưởng đến quá trình thanh toán. Anh/chị vui lòng không cần phải thực hiện bất kỳ bước thanh toán nào cho đơn hàng này.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailRefunMoMo(String toEmail, String fullName, String nameStore, String note, String orderStatus) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng yêu cầu trả hàng của anh/chị đã bị từ chối bởi người bán. Lý do từ chối là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã chọn phương thức thanh toán online qua MoMo. Để được hoàn tiền, anh/chị vui lòng liên hệ với chúng tôi qua các phương thức dưới đây để chúng tôi hỗ trợ hoàn lại số tiền đã thanh toán.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailRefunVNPay(String toEmail, String fullName, String nameStore, String note, String orderStatus) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("YÊU CẦU HUỶ ĐƠN HÀNG TỪ CỬA HÀNG " + nameStore.toUpperCase());
			String reason = note.split(":")[1].trim();
			String htmlContent = "<style>"
					+ "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }"
					+ "p { font-size: 16px; margin-bottom: 15px; }"
					+ "ul { list-style-type: none; padding: 0; margin-bottom: 20px; }"
					+ "ul li { font-size: 16px; margin: 5px 0; }"
					+ "strong { color: #2c3e50; }"
					+ ".header { font-size: 18px; font-weight: bold; color: #2980b9; margin-bottom: 20px; }"
					+ ".footer { font-size: 14px; color: #7f8c8d; margin-top: 30px; }"
					+ ".content { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
					+ "</style>"
					+ "<body>"
					+ "<div class='content'>"
					+ "<p class='header'>Kính chào anh/chị " + fullName + ",</p>"
					+ "<p>Chúng tôi rất tiếc phải thông báo rằng yêu cầu trả hàng của anh/chị đã bị từ chối bởi người bán. Lý do từ chối là: <strong>"
					+ reason + "</strong>.</p>"
					+ "<p>Vì anh/chị đã chọn phương thức thanh toán online qua VNPay. Để được hoàn tiền, anh/chị vui lòng liên hệ với chúng tôi qua các phương thức dưới đây để chúng tôi hỗ trợ hoàn lại số tiền đã thanh toán.</p>"
					+ "<p>Để thuận tiện trong việc giải quyết, xin vui lòng liên hệ với chúng tôi qua:</p>"
					+ "<ul>"
					+ "<li><strong>Hotline:</strong> 0763889837</li>"
					+ "<li><strong>Email:</strong> perwattcompany@gmail.com</li>"
					+ "</ul>"
					+ "<p>Chúng tôi xin chân thành cảm ơn sự hợp tác của anh/chị và mong nhận được phản hồi từ anh/chị sớm nhất có thể.</p>"
					+ "<p class='footer'>Trân trọng,</p>"
					+ "<p class='footer'>Đội ngũ hỗ trợ khách hàng</p>"
					+ "</div>"
					+ "</body>";

			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
