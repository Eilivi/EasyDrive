package com.peirong.service;

import com.peirong.entity.Email;

public interface SendEmailService {
    void sendSimpleEmail(Email mailRequest);
}
