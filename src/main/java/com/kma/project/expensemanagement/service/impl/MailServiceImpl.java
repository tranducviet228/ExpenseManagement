package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    @Value("${email.fromEmail}")
    private String fromEmail;

    @Value("${email.password}")
    private String password;

    final String subject = "[Công ty cổ phần Đức Việt]";
    final String body = "Xin chào Trần Việt,\n" +
            "\n" +
            "Mã xác nhận kích hoạt tài khoản của bạn là:\n" +
            "\n" +
            "994638\n" +
            "\n" +
            "Mã này sẽ hết hạn sau 5 phút, vui lòng không tiết lộ mã xác nhận của bạn cho bất kỳ ai.\n" +
            "\n" +
            "Cảm ơn bạn đã sử dụng sản phẩm của CUDAU.\n" +
            "Trân trọng.";

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
        Session sesion = Session.getInstance(properties, auth);

        try {
            MimeMessage msg = new MimeMessage(sesion);
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
