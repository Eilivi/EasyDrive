package com.peirong.service.Implement;

import com.peirong.service.SendEmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Peirong
 */
@Service
public class SendEmailServiceImpl implements SendEmailService {

    private final JavaMailSender javaMailSender;

    public SendEmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

}

