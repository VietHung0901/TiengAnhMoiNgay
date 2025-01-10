package Project.TiengAnhMoiNgay.Services;

import Project.TiengAnhMoiNgay.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, User user, String confirmationUrl) {
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        // Nội dung email tùy chỉnh
//        String messageContent = "Xin chào " + user.getHoten() + ",\n\n"
//                + "Cảm ơn bạn đã đăng ký tài khoản ở Hệ thống hỗ trợ cuộc thi của chúng tôi.\n"
//                + "Vui lòng nhấp vào liên kết dưới đây để xác nhận email của bạn:\n\n"
//                + confirmationUrl + "\n\n"
//                + "Thông tin của bạn:\n"
//                + "Email: " + user.getEmail() + "\n"
//                + "SDT: " + user.getPhone() + "\n"
//                + "Trường: " + user.getTruong().getTenTruong() + "\n"
//                + "Ngày sinh: " + user.getNgaySinh() + "\n"
//                + "Trân trọng,\nĐội ngũ hỗ trợ";
//
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(messageContent);
//        mailSender.send(message);
    }
}
