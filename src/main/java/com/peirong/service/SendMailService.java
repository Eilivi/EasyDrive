package com.peirong.service;

import com.peirong.entity.Email;

public interface SendMailService {
    void sendSimpleMail(Email mailRequest);
    void sendHtmlMail(Email mailRequest);
}
