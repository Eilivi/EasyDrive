package com.peirong.controller;

import com.peirong.entity.Email;
import com.peirong.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send-mail")
public class SendEmailController {
    @Autowired
    private SendMailService sendMailService;

    @PostMapping("/simple")
    public void SendSimpleMessage(@RequestBody Email mailRequest) {
        sendMailService.sendSimpleMail(mailRequest);
    }

    @PostMapping("/html")
    public void SendHtmlMessage(@RequestBody Email mailRequest) {
        sendMailService.sendHtmlMail(mailRequest);
    }

    @PostMapping("/send-email")
    public String send(@RequestBody Email email) {

        //String code =

        return "0";
    }
}




