package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peirong.entity.Account;
import com.peirong.service.AccountService;
import com.peirong.util.CaptchaUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class AccountController {
    @Resource
    AccountService accountService;

    @PostMapping("/login") //
    public String login(@RequestBody Account account, String authentic, HttpServletRequest request){
        String auth = (String) request.getSession().getAttribute("captcha");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, account.getUsername()).or().eq(Account::getEmail, account.getUsername());
        Account result = accountService.getOne(queryWrapper);

        if (result == null) {
            return "-1";
        }

        if (!auth.equalsIgnoreCase(authentic)) {
            return "-2";
        }

        if (result.getPassword().equals(account.getPassword()) && auth.equalsIgnoreCase(authentic)) {
            request.getSession().removeAttribute("captcha");

            return result.getUsername();
        }
        return "-1";
    }

    @GetMapping("/getAuthentic")
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


    @PostMapping("/register")
    public String register(@RequestBody Account account,String verify,HttpServletRequest request) {
        String captcha = (String) request.getSession().getAttribute("captcha");
        if (verify.equals(captcha)) {
            return accountService.save(account) ? "0" : "-1";
        }
        return "-1";
    }

    @GetMapping("/check")
    public String check(String phoneOrEmail, String type) {
        Account result;
        if (type.equals("phone")) {
            LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Account::getPhone, phoneOrEmail);
            result = accountService.getOne(queryWrapper);
        } else {
            LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Account::getEmail, phoneOrEmail);
            result = accountService.getOne(queryWrapper);
        }
        if (result != null)
            return "-1";
        else return "0";
    }
}