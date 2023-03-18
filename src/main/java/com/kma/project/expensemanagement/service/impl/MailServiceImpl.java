package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.entity.UserEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.repository.UserRepository;
import com.kma.project.expensemanagement.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService {

    String subject = "[Công ty cổ phần Đức Việt]";

    @Value("${email.fromEmail}")
    private String fromEmail;

    @Value("${email.password}")
    private String password;
    String body = "Xin chào username,\n" +
            "\n" +
            "Mã xác nhận kích hoạt tài khoản của bạn là:\n" +
            "\n" +
            "otp\n" +
            "\n" +
            "Mã này sẽ hết hạn sau 5 phút, vui lòng không tiết lộ mã xác nhận của bạn cho bất kỳ ai.\n" +
            "\n" +
            "Cảm ơn bạn đã sử dụng sản phẩm của CUDAU.\n" +
            "Trân trọng.";
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void sendMail(String toEmail) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        // save otp to db
        UserEntity userEntity = userRepository.findByEmail(toEmail)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.email-not-found")).build());
        userEntity.setOtp(otp);
        userRepository.save(userEntity);

        body = body.replace("otp", otp);
        body = body.replace("username", userEntity.getUsername());
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(fromEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
        } catch (Exception e) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.email-system-is-error")).build();
        }
        body = body.replace(otp, "otp");


    }
}
