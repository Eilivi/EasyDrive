package com.peirong.service.Implement;

import com.peirong.service.SendEmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Peirong
 */
@Service
public class SendEmailImpl implements SendEmailService {
    public final JavaMailSender javaMailSender;
    public SendEmailImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
}

