package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peirong.entity.RestResponse;
import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import com.peirong.service.UserService;
import com.peirong.util.SendEmailAndMessage;
import com.peirong.util.UUIDUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("after")
public class AfterLoginController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private BCryptPasswordEncoder encoder;
    @Resource
    private SendEmailAndMessage send;
    @Resource
    private HttpServletRequest request;

    private static final String UPLOAD_DIR = "/Users/peirong/avatar";

    @PostMapping("UpdateAvatar")
    public RestResponse<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        Account account = (Account) request.getSession().getAttribute("account");
        Long id = account.getId();

        String filename = file.getOriginalFilename();
        assert filename != null;
        String[] suffix = filename.split("\\.");
        int suffixIndex = suffix.length - 1;
        //雪花算法生成文件名
        String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
        file.transferTo(new File(UPLOAD_DIR + name));
        try {
            account.setId(id);
            account.setAvatar(name);
            userService.saveAccount(account);
            return RestResponse.success("上传成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.failure(401,"上传失败");
    }

    @PostMapping("SendMessage")
    public RestResponse<String> sendMessage(@RequestBody Map<String,String> map, HttpServletResponse response) throws Exception {
        String number = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, number);
        if (userService.getOne(queryWrapper) != null) {
            return RestResponse.failure(401, "手机号已被占用");
        } else {
            send.sendMessage(number);
            return RestResponse.success("发送成功");
        }
    }

    @PostMapping("UpdatePhone")
    public RestResponse<String> updatePhone(@RequestBody Map<String,String> map, HttpServletResponse response) {
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + map.get("phone"));
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, map.get("phone"));
        Account checkPhoneHaveOrNot = userService.getOne(queryWrapper);

        if (checkPhoneHaveOrNot != null) {
            return RestResponse.failure(401, "手机号已被占用");
        } else if (codeFromPhone.matches(map.get("code"))) {
            Account account = (Account) request.getSession().getAttribute("account");
            account.setPhone(map.get("phone"));
            userService.updateById(account);
            return RestResponse.success("修改成功，请重新登录");
        }
        return RestResponse.failure(401,"验证码错误");
    }


    @PostMapping("SendEmail")
    public RestResponse<String> sendEmail(@RequestBody Map<String,String> map, HttpServletResponse response) throws Exception {
        String email = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, email);
        if (userService.getOne(queryWrapper) != null) {
            return RestResponse.failure(401, "邮箱已被占用");
        } else {
            send.sendEmail(email);
            return RestResponse.success("发送成功");
        }
    }
    @PostMapping("UpdateEmail")
    public RestResponse<String> updateEmail(@RequestBody Map<String, String> map, HttpServletResponse response, HttpServletRequest request) {

        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + map.get("email"));
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, map.get("email"));
        Account checkEmailHaveOrNot = userService.getOne(queryWrapper);
        if (checkEmailHaveOrNot != null) {
            return RestResponse.failure(401, "邮箱已被占用");
        } else if (codeFromEmail.matches(map.get("code"))) {
            Account account = (Account) request.getSession().getAttribute("account");
            account.setEmail(map.get("email"));
            userService.updateById(account);
            return RestResponse.success("修改成功，请重新登录");
        }
        return RestResponse.failure(401,"验证码错误");
    }

    @PostMapping("UpdatePassword")
    public RestResponse<String> updatePassword(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        Account account = (Account) request.getSession().getAttribute("account");
        if (encoder.matches(newPassword, account.getPassword())) {
            return RestResponse.failure(401, "新密码不能与旧密码相同");
        } else if (encoder.matches(oldPassword, account.getPassword())) {
            account.setPassword(encoder.encode(newPassword));
            userService.updateById(account);
            return RestResponse.success("修改成功，请重新登录");
        } else {
            return RestResponse.failure(401, "原密码错误");
        }
    }

    @PostMapping("UpdateUsername")
    public RestResponse<String> updateUsername(@RequestBody Map<String, String> map, HttpServletResponse response) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUsername, map.get("username"));
        Account checkUsernameHaveOrNot = userService.getOne(queryWrapper);
        if (checkUsernameHaveOrNot != null) {
            return RestResponse.failure(401, "昵称已被占用");
        } else {
            Account account = (Account) request.getSession().getAttribute("account");
            account.setUsername(map.get("username"));
            userService.updateById(account);
            return RestResponse.success("修改成功");
        }
    }
}
