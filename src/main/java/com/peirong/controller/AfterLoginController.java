package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peirong.entity.FileEntity;
import com.peirong.entity.RestBeanResponse;
import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import com.peirong.service.UserService;
import com.peirong.util.Result;
import com.peirong.util.SendEmailAndMessage;
import com.peirong.util.SnowflakeIdUtils;
import com.peirong.util.UUIDUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/after")
public class AfterLoginController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private BCryptPasswordEncoder encoder;
    @Resource
    private SendEmailAndMessage send;

    private static final String UPLOAD_DIR = "/Users/peirong/avatar";

    @PostMapping("/UpdateAvatar")
    public RestBeanResponse<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            return RestBeanResponse.success("上传成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestBeanResponse.failure(401,"上传失败");
    }

    @PostMapping("SendMessage")
    public RestBeanResponse<String> sendMessage(@RequestBody Map<String,String> map, HttpServletResponse response) throws Exception {
        String number = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, number);
        if (userService.getOne(queryWrapper) != null) {
            return RestBeanResponse.failure(401, "手机号已被占用");
        } else {
            send.sendMessage(number);
            return RestBeanResponse.success("发送成功");
        }
    }

    @PostMapping("UpdatePhone")
    public RestBeanResponse<String> updatePhone(@RequestBody Map<String,String> map, HttpServletResponse response, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + map.get("phone"));
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, map.get("phone"));
        Account checkPhoneHaveOrNot = userService.getOne(queryWrapper);
        if (checkPhoneHaveOrNot != null) {
            return RestBeanResponse.failure(401, "手机号已被占用");
        } else if (codeFromPhone.matches(map.get("code"))) {
            account.setId(account.getId());
            account.setPhone(map.get("phone"));
            userService.updateById(account);
            return RestBeanResponse.success("修改成功");
        }
        return RestBeanResponse.failure(401,"验证码错误");
    }


    @PostMapping("SendEmail")
    public RestBeanResponse<String> sendEmail(@RequestBody Map<String,String> map, HttpServletResponse response) throws Exception {
        String email = map.get("account");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, email);
        if (userService.getOne(queryWrapper) != null) {
            return RestBeanResponse.failure(401, "邮箱已被占用");
        } else {
            send.sendEmail(email);
            return RestBeanResponse.success("发送成功");
        }
    }
    @PostMapping("UpdateEmail")
    public RestBeanResponse<String> updateEmail(@RequestBody Map<String, String> map, HttpServletResponse response, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        String codeFromEmail = (String) request.getSession().getAttribute("email-code-" + map.get("email"));
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail, map.get("email"));
        Account checkEmailHaveOrNot = userService.getOne(queryWrapper);
        if (checkEmailHaveOrNot != null) {
            return RestBeanResponse.failure(401, "邮箱已被占用");
        } else if (codeFromEmail.matches(map.get("code"))) {
            account.setId(account.getId());
            account.setEmail(map.get("email"));
            userService.updateById(account);
            return RestBeanResponse.success("修改成功");
        }
        return RestBeanResponse.failure(401,"验证码错误");
    }

    @PostMapping("UpdatePassword")
    public RestBeanResponse<String> updatePassword(@RequestBody Map<String, String> map, HttpServletResponse response, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        Long id = account.getId();
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");

        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();

        if (encoder.matches(newPassword, account.getPassword())) {
            return RestBeanResponse.failure(401, "新密码不能与旧密码相同");
        } else if (encoder.matches(oldPassword, account.getPassword())) {
            account.setId(id);
            account.setPassword(encoder.encode(newPassword));
            userService.updateById(account);
            return RestBeanResponse.success("修改成功");
        } else {
            return RestBeanResponse.failure(401, "原密码错误");
        }
    }

    @PostMapping("UpdateUsername")
    public RestBeanResponse<String> updateUsername(@RequestBody Map<String, String> map, HttpServletResponse response, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        Long id = account.getId();
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUsername, map.get("username"));
        Account checkUsernameHaveOrNot = userService.getOne(queryWrapper);
        if (checkUsernameHaveOrNot != null) {
            return RestBeanResponse.failure(401, "昵称已被占用");
        } else {
            account.setId(id);
            account.setUsername(map.get("username"));
            userService.updateById(account);
            return RestBeanResponse.success("修改成功");
        }
    }
}
