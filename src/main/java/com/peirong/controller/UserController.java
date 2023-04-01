package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.User;
import com.peirong.entity.Email;
import com.peirong.service.AccountService;
import com.peirong.util.CaptchaUtil;
import com.peirong.util.EmailUtil;
import com.peirong.util.SendSmsUtil;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

@RestController
public class UserController {
    @Value("ieep.roon@gmail.com")
    private String sendMailer;
    @Resource
    AccountService accountService;
    @Autowired
    private JavaMailSender javaMailSender;
    @PostMapping("/login")
    public String login(@RequestBody User user, String authentic, HttpServletRequest request) {

        String auth = (String) request.getSession().getAttribute("captcha");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = accountService.getOne(queryWrapper);

        if (!auth.equalsIgnoreCase(authentic)) {
            return "-2";
        } else if (result == null) {
            return "-3";
        } else if (result.getPassword().equals(user.getPassword()) && auth.equalsIgnoreCase(authentic)) {
            request.getSession().removeAttribute("captcha");
            return result.getUsername();
        }
        return "-1";
    }

    @GetMapping("/get-authentic")
    public String getAuthentic(HttpServletRequest request, HttpServletResponse response) {
        CaptchaUtil c = new CaptchaUtil(130, 40);
        String captchastr = c.getCode();
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captchastr);
        try {
            c.write(response.getOutputStream());
        } catch (IOException e) {
            session.removeAttribute("captcha");
        }
        System.out.println(captchastr);
        return "success";
    }

    @GetMapping("/check")
    public String check(String phoneOrEmail, String type) {
        User result;
        if (type.equals("phone")) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phoneOrEmail);
            result = accountService.getOne(queryWrapper);
        } else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, phoneOrEmail);
            result = accountService.getOne(queryWrapper);
        }
        if (result != null)
            return "-1";
        else return "0";
    }

    @GetMapping("/send-email")
    public String send(String email, HttpServletRequest request, HttpServletResponse response) {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.createCode();
        String code = emailUtil.getCode();

        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        session.setAttribute("codebase", code);

        try {
            Email mailRequest = new Email();
            mailRequest.setSendTo(email);
            mailRequest.setSubject("账户安全代码");
            mailRequest.setText("您的验证码为：" + code + "，有效时间为5分钟。");
            emailUtil.checkMail(mailRequest);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sendMailer);
            message.setTo(mailRequest.getSendTo());
            message.setSubject(mailRequest.getSubject());
            message.setText(mailRequest.getText());
            message.setSentDate(new Date());
            new Thread(() -> javaMailSender.send(message)).start();
        } catch (MailAuthenticationException e) {
            session.removeAttribute("codebase");
            return "-1";
        }
        return "0";
    }


    // TODO: 发送手机验证码
    @RequestMapping(value="/send-message")
    @ResponseBody
    public String sendCodeAgain(String phone, HttpServletRequest request, HttpServletResponse response) {
        String[] Phones = {"+86" + phone};
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        session.setAttribute("phone-code", phone);

        try {
            new Thread(() -> {
                SendStatus[] ret = SendSmsUtil.sendSms(Phones);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
        return "0";
    }

    @PostMapping("/register")
    public String register(@RequestBody User user, String verify, HttpServletRequest request) {
        String codebase = (String) request.getSession().getAttribute("codebase");
        if (!verify.equals(codebase)) {
            return accountService.save(user) ? "0" : "-1";
        }
        boolean result = accountService.save(user);
        if (result) {
            return "0";
        } else {
            return "-1";
        }
    }

    // TODO: 找回密码
    @PostMapping("/recover")
    public String recover(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = accountService.getOne(queryWrapper);
        return "0";
    }

    // TODO：修改密码
    @PostMapping("/change-password")
    public String change(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = accountService.getOne(queryWrapper);
        return "0";
    }
}