package com.peirong.util;

import com.peirong.entity.Email;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.Random;

@Data
public class EmailUtil {
    private String code;

    public void checkMail(Email mailRequest) {
        Assert.notNull(mailRequest, "邮件请求不能为空");
        Assert.notNull(mailRequest.getSendTo(), "邮件收件人不能为空");
        Assert.notNull(mailRequest.getSubject(), "邮件主题不能为空");
        Assert.notNull(mailRequest.getText(), "邮件收件人不能为空");
    }

    public void createCode() {
        String code = String.format("%05d",new Random().nextInt(100000));
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
