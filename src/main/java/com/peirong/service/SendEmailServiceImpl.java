package com.peirong.service;

import com.peirong.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    //注入邮件工具类
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    public void checkMail(Email mailRequest) {
        Assert.notNull(mailRequest,"邮件请求不能为空");
        Assert.notNull(mailRequest.getSendTo(), "邮件收件人不能为空");
        Assert.notNull(mailRequest.getSubject(), "邮件主题不能为空");
        Assert.notNull(mailRequest.getText(), "邮件收件人不能为空");
    }

    @Override
    public void sendSimpleEmail(Email mailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        checkMail(mailRequest);
        message.setFrom(sendMailer);
        message.setTo(mailRequest.getSendTo().split(","));
        message.setSubject(mailRequest.getSubject());
        message.setText(mailRequest.getText());
        message.setSentDate(new Date());
        javaMailSender.send(message);
    }
}

