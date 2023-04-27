package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.Account;
import com.peirong.service.UserService;
import com.peirong.util.*;
import com.squareup.okhttp.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/sendMessageOrEmail")
    public boolean sendMessageOrEmail(@RequestBody Map<String, String> map, String recover) throws Exception {

        String account = map.get("account");
        String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
        String PHONE_REGEX = "^1\\d{10}$";

        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, account).or().eq(Account::getPhone, account);
        Account result = userService.getOne(queryWrapper);

        if ((recover == null && account.matches(PHONE_REGEX) && result == null) ||
                (Objects.equals(recover, "recover") && account.matches(PHONE_REGEX) && result != null)) {
            int vode = ValidateCode.generateValidateCode(6);
            String code = String.valueOf(vode);

            System.out.println("发送验证码：" + code);

            OkHttpClient client = new OkHttpClient();
            Request postRequest = new Request.Builder().url(sendSmsUtil.postURL(account, code)).get().build();
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(300);
            executorService.execute(() -> {
                try {
                    String sendTool = client.newCall(postRequest).execute().body().string();
                    session.setAttribute("phone-code-" + account, code);
                } catch (IOException e) {
                    session.removeAttribute("phone-code-" + account);
                    throw new RuntimeException(e);
                }
            });
            return true;
        } else if ((recover == null && account.matches(EMAIL_REGEX) && result == null) ||
                (Objects.equals(recover, "recover") && account.matches(EMAIL_REGEX) && result != null)) {
            String code = String.format("%05d", new Random().nextInt(100000));
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(60);
            System.out.println(account);
            try {
                executorService.execute(() -> sendEmailUtil.sendEmail(account, code));
                session.setAttribute("email-code-" + account, code);
                return true;
            } catch (Exception ex) {
                session.removeAttribute("email-code-");
                return false;
            }
        }
        return false;
    }

    @GetMapping("/checkIfThereIsAUser/{username}")
    public boolean checkIfThereIsAUser(@PathVariable("username") String username) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUsername, username);
        Account checkUsernameHaveOrNot = userService.getOne(queryWrapper);
        return checkUsernameHaveOrNot != null;
    }

    @PostMapping("/register/{verify}/{account}")
    public boolean register(@RequestBody Account user,
                            @PathVariable("verify") String verify,
                            @PathVariable("account") String account) {
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + account);
        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + account);
        if (verify.equals(codeFromPhone) || verify.equals(codeFromEmail)) {
            if (user.getEmail() == null) {
                user.setPhone(account);
            } else if (user.getPhone() == null) {
                user.setEmail(account);
            }
            return userService.saveAccount(user);
        }
        return false;
    }

    @PostMapping("/recover/{verify}/{account}")
    public boolean recover(@RequestBody Map<String, String> map,
                           @PathVariable("verify") String verify,
                           @PathVariable("account") String account) {
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + account);
        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + account);

        if (verify.equals(codeFromPhone) || verify.equals(codeFromEmail)) {
            Account userNew = new Account();
            userNew.setPassword(encoder.encode(map.get("password")));
            System.out.println(map.get("password"));
            QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", map.get("account")).or().eq("email", map.get("account"));
            userService.update(userNew, queryWrapper);
        } else {
            return false;
        }
        return true;
    }



}