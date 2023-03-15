package com.peirong;

import com.peirong.entity.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

import java.util.Date;

@SpringBootTest
class EasyDriveApplicationTests {
    public void checkMail(Email mailRequest) {
        Assert.notNull(mailRequest,"邮件请求不能为空");
        Assert.notNull(mailRequest.getSendTo(), "邮件收件人不能为空");
        Assert.notNull(mailRequest.getSubject(), "邮件主题不能为空");
        Assert.notNull(mailRequest.getText(), "邮件收件人不能为空");
    }
    @Value("${spring.mail.username}")
    private String sendMailer;
    @Autowired
    private JavaMailSender javaMailSender;
    @Test
    void contextLoads() {
        Email mailRequest = new Email();
        mailRequest.setSendTo("201910311201@stu.shmtu.edu.cn");
        mailRequest.setText("\uD83D\uDE18");

        mailRequest.setSubject("test");
        SimpleMailMessage message = new SimpleMailMessage();
        checkMail(mailRequest);
        //邮件发件人
        message.setFrom(sendMailer);
        //邮件收件人 1或多个
        message.setTo(mailRequest.getSendTo().split(","));
        //邮件主题
        message.setSubject(mailRequest.getSubject());
        //邮件内容
        message.setText(mailRequest.getText());
        //邮件发送时间
        message.setSentDate(new Date());

        javaMailSender.send(message);
    }

}
