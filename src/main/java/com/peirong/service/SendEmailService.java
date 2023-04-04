package com.peirong.service;

import com.peirong.entity.Email;

/**
 * @author Peirong
 */
public interface SendEmailService {
    void sendSimpleEmail(Email mailRequest);
}
