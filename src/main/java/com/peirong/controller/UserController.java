package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.User;
import com.peirong.mapper.UserMapper;
import com.peirong.service.UserService;
import com.peirong.util.*;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * @author Peirong
 */
@RestController
public class UserController {
    @Resource
    UserService userService;
    @Autowired
    SendEmailUtil sendEmailUtil;
@Autowired
    UserMapper userMapper;
    @Autowired
    private ExecutorService executorService;

    @PostMapping("/login")
    public String login(@RequestBody User user, String authentic, HttpServletRequest request) {

        String auth = (String) request.getSession().getAttribute("captcha");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = userService.getOne(queryWrapper);

        if (!auth.equalsIgnoreCase(authentic)) {
            return "-2";
        } else if (result == null) {
            return "-3";
        } else if (BCrypt.checkpw(user.getPassword(),result.getPassword()) && auth.equalsIgnoreCase(authentic)) {
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
        if ("phone".equals(type)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phoneOrEmail);
            result = userService.getOne(queryWrapper);
        } else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, phoneOrEmail);
            result = userService.getOne(queryWrapper);
        }
        if (result != null) {
            return "-1";
        } else {
            return "0";
        }
    }

    @GetMapping("/send-email")
    public String sendEmail(String account, HttpServletRequest request) {
        String code = String.format("%05d",new Random().nextInt(100000));
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        session.setAttribute("email-code-" + account, code);

        try {
            executorService.execute(() -> {
                sendEmailUtil.sendEmail(account,code);
            });
            return "0";
        } catch (Exception ex) {
            session.removeAttribute("email-code");
            return "-1";
        }
    }

    @RequestMapping(value = "/send-message")
    @ResponseBody
    public String sendMessage(String account, HttpServletRequest request) {

        String[] phones = {"+86" + account};
        int vode = ValidateCode.generateValidateCode(6);
        String code = String.valueOf(vode);
        Constants.voicode = code;
        System.out.println(code);
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);
        session.setAttribute("phone-code-" + account, code);
        System.out.println(session.getId());
        executorService.execute(() -> {
            SendStatus[] ret = SendSmsUtil.sendSms(phones, code);
        });
        return "0";
    }

    @PostMapping("/register")
    public String register(@RequestBody User user, String account, @RequestParam("verify") String verify, HttpServletRequest request) {
        String code = (String) request.getSession().getAttribute("phone-code-" + account);
        String code1 = (String) request.getSession().getAttribute("email-code-" + account);
        System.out.println(code1);
        System.out.println(verify);
        System.out.println(code);
        if (verify.equals(code) || verify.equals(code1)) {
            boolean result = userService.saveAccount(user);
            if (result) {
                return "0";
            } else {
                return "-1";
            }
        } else {
            return "-2";
        }
    }

    @PostMapping("/recover")
    public String recover(@RequestBody User user, String account, @RequestParam("verify") String verify, HttpServletRequest request) {
        String code = (String) request.getSession().getAttribute("phone-code-" + account);
        String code1 = (String) request.getSession().getAttribute("email-code-" + account);

        if (verify.equals(code) || verify.equals(code1)) {
            User userNew = new User();
            userNew.setId(user.getId());
            userNew.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", user.getPhone()).or().eq("email", user.getEmail());
            userService.update(userNew,queryWrapper);
        } else {
            return "-1";
        }
        return "0";
    }

    @PostMapping("/change-password")
    public String change(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = userService.getOne(queryWrapper);
        return "0";
    }
}