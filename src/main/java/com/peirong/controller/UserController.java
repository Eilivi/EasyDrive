package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.User;
import com.peirong.service.UserService;
import com.peirong.util.*;
import com.squareup.okhttp.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Resource
    SendEmailUtil sendEmailUtil;

    @Resource
    private ExecutorService executorService;

    @PostMapping("/login/{authentic}")
    public String login(@RequestBody User user, @PathVariable("authentic") String authentic, HttpServletRequest request) {

        String auth = (String) request.getSession().getAttribute("captcha");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getUsername()).or().eq(User::getEmail, user.getUsername());
        User result = userService.getOne(queryWrapper);

        if (!auth.equalsIgnoreCase(authentic)) {
            return "-2";
        } else if (result == null) {
            return "-3";
        } else if (BCrypt.checkpw(user.getPassword(), result.getPassword()) && auth.equalsIgnoreCase(authentic)) {
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
        try {
            c.write(response.getOutputStream());
            session.setAttribute("captcha", captchastr);
        } catch (IOException e) {
            session.removeAttribute("captcha");
        }
        System.out.println(captchastr);
        return "success";
    }

    @GetMapping("/check/{phoneOrEmail}/{type}")
    public String check(@PathVariable("phoneOrEmail") String phoneOrEmail, @PathVariable("type") String type) {
        User result;
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if ("phone".equals(type)) {
            queryWrapper.eq(User::getPhone, phoneOrEmail);
        } else {
            queryWrapper.eq(User::getEmail, phoneOrEmail);
        }
        result = userService.getOne(queryWrapper);
        if (result != null) {
            return "-1";
        } else {
            return "0";
        }
    }

    @GetMapping("/send-email/{account}")
    public String sendEmail(@PathVariable("account") String account, HttpServletRequest request) {
        String code = String.format("%05d", new Random().nextInt(100000));
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);

        try {
            executorService.execute(() -> sendEmailUtil.sendEmail(account, code));
            session.setAttribute("email-code-" + account, code);
            return "0";
        } catch (Exception ex) {
            session.removeAttribute("email-code");
            return "-1";
        }
    }

    @RequestMapping(value = "/send-message/{account}")
    @ResponseBody
    public String sendMessage(@PathVariable("account") String account, HttpServletRequest request) throws Exception {
        int vode = ValidateCode.generateValidateCode(6);
        String code = String.valueOf(vode);
        OkHttpClient client = new OkHttpClient();
        Request postRequest = new Request.Builder().url(SendSmsUtil.postURL(account, code)).get().build();
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300);

        executorService.execute(() -> {
            try {
                String result = client.newCall(postRequest).execute().body().string();
                session.setAttribute("phone-code-" + account, code);
                System.out.println(result);
            } catch (IOException e) {
                session.removeAttribute("phone-code-" + account);
                throw new RuntimeException(e);
            }
        });
        return "0";
    }

    @PostMapping("/register/{verify}/{account}")
    public String register(@RequestBody User user, @PathVariable("account") String account, @PathVariable("verify") String verify, HttpServletRequest request) {
        String code = (String) request.getSession().getAttribute("phone-code-" + account);
        String code1 = (String) request.getSession().getAttribute("email-code-" + account);

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

    @PostMapping("/recover/{verify}/{account}")
    public String recover(@RequestBody User user, @PathVariable("account") String account, @PathVariable("verify") String verify, HttpServletRequest request) {
        String code = (String) request.getSession().getAttribute("phone-code-" + account);
        String code1 = (String) request.getSession().getAttribute("email-code-" + account);

        if (verify.equals(code) || verify.equals(code1)) {
            User userNew = new User();
            userNew.setId(user.getId());
            userNew.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", user.getPhone()).or().eq("email", user.getEmail());
            userService.update(userNew, queryWrapper);
        } else {
            return "-1";
        }
        return "0";
    }
}