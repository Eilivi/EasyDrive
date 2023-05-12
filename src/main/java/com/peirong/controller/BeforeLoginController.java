package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.Account;
import com.peirong.entity.RestBeanResponse;
import com.peirong.service.UserService;
import com.peirong.util.*;
import com.squareup.okhttp.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * @author Peirong
 */
@RestController
@RequestMapping("/before")
public class BeforeLoginController {
    @Resource
    private UserService userService;
    @Resource
    private SendEmailUtil sendEmailUtil;
    @Resource
    private SendSmsUtil sendSmsUtil;
    @Resource
    private HttpServletRequest request;
    @Resource
    private ExecutorService executorService;
    @Resource
    private BCryptPasswordEncoder encoder;
    final static String PHONE_REGEX = "^1\\d{10}$";
    final static String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    @Resource
    private SendEmailAndMessage send;

    @PostMapping("SendMessageToRegister")
    public RestBeanResponse<String> sendMessage(@RequestBody Map<String, String> map) throws Exception {
        String account = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, account);
        if (userService.getOne(queryWrapper) != null)
            return RestBeanResponse.failure(401, "手机号已被注册");
        else if (account.matches(PHONE_REGEX)) {
            send.sendMessage(account);
            return RestBeanResponse.success("发送成功");
        }
        return RestBeanResponse.failure(401, "发送失败，请联系管理员");
    }

    @PostMapping("SendMessageToRecover")
    public RestBeanResponse<String> sendMessageToRecover(@RequestBody Map<String, String> map, HttpServletResponse response) throws Exception {
        String account = map.get("account");
        System.out.println(account);
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, account);
        if (userService.getOne(queryWrapper) == null)
            return RestBeanResponse.failure(401, "手机号未注册");
        else if (account.matches(PHONE_REGEX)) {
            send.sendMessage(account);
            return RestBeanResponse.success("发送成功");
        }
        return RestBeanResponse.failure(401, "发送失败，请联系管理员");
    }

    @PostMapping("SendEmailToRegister")
    public RestBeanResponse<String> sendEmailToRegister(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String account = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, account);
        if (userService.getOne(queryWrapper) != null)
            return RestBeanResponse.failure(401, "邮箱已被占用");
        else if (account.matches(EMAIL_REGEX)) {
            send.sendEmail(account);
            return RestBeanResponse.success("发送成功");
        }
        return RestBeanResponse.failure(401, "发送失败");
    }

    @PostMapping("SendEmailToRecover")
    public RestBeanResponse<String> sendEmailToRecover(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String account = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, account);
        if (userService.getOne(queryWrapper) == null)
            return RestBeanResponse.failure(401, "邮箱未注册");
        else if (account.matches(EMAIL_REGEX)) {
            send.sendEmail(account);
            return RestBeanResponse.success("发送成功");
        }
        return RestBeanResponse.failure(401, "发送失败");
    }

    @GetMapping("CheckIfThereIsAUser/{username}")
    public boolean checkIfThereIsAUser(@PathVariable("username") String username) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUsername, username);
        Account checkUsernameHaveOrNot = userService.getOne(queryWrapper);
        return checkUsernameHaveOrNot != null;
    }

    @PostMapping("register")
    public RestBeanResponse<String> register(@RequestBody Map<String, String> map) {
        String verify = map.get("verify");
        System.out.println(verify);

        String account = map.get("account");
        System.out.println(account);

        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + account);
        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + account);
        Account newAccount = new Account();
        if (codeFromPhone == null && codeFromEmail == null)
            return RestBeanResponse.failure(401,"验证码已过期");
        else if (verify.equals(codeFromPhone) || verify.equals(codeFromEmail)) {
            if (account.matches(EMAIL_REGEX)) {
                newAccount.setEmail(map.get("account"));
                newAccount.setUsername(map.get("username"));
                newAccount.setPassword(map.get("password"));
            } else if (account.matches(PHONE_REGEX)) {
                newAccount.setPhone(map.get("account"));
                newAccount.setUsername(map.get("username"));
                newAccount.setPassword(map.get("password"));
            }
            userService.saveAccount(newAccount);
            return RestBeanResponse.success("注册成功");
        } else return RestBeanResponse.failure(401,"验证码错误");
    }

    @PostMapping("ChangePasswordToRecover")
    public RestBeanResponse<String> recover(@RequestBody Map<String, String> map) {
        String account = map.get("account");
        String verify = map.get("verify");
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + account);
        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + account);

        if (codeFromPhone == null && codeFromEmail == null)
            return RestBeanResponse.failure(401,"验证码已过期");
        else if (verify.equals(codeFromPhone) || verify.equals(codeFromEmail)) {
            Account setNewPassword = new Account();
            setNewPassword.setPassword(encoder.encode(map.get("password")));
            System.out.println(map.get("password"));
            QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", map.get("account")).or().eq("email", map.get("account"));
            userService.update(setNewPassword, queryWrapper);
        } else {
            return RestBeanResponse.failure(401,"验证码错误");
        }
        return RestBeanResponse.success("修改成功，请登录");
    }
}