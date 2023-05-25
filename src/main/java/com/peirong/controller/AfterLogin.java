package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peirong.entity.RestResponse;
import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import com.peirong.service.ResponseService;
import com.peirong.service.UserService;
import com.peirong.util.SendEmailMessage;
import com.peirong.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("after")
public class AfterLogin {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private BCryptPasswordEncoder encoder;
    @Resource
    private SendEmailMessage send;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisTemplate redisTemplate;

    @Value("${uploadAvatar}")
    private String uploadAvatar;

    @PostMapping("avatar")
    public Map<String, Object> uploadAvatar(MultipartFile file, String id) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Account account = (Account) redisTemplate.opsForValue().get("account_" + id);
        String filename = file.getOriginalFilename();
        assert filename != null;
        String[] suffix = filename.split("\\.");
        int suffixIndex = suffix.length - 1;
        if (!suffix[suffixIndex].equals("jpg") && !suffix[suffixIndex].equals("png") && !suffix[suffixIndex].equals("jpeg")) {
            map.put(ResponseService.CODE, 510);
            map.put(ResponseService.SUCCESS, false);
            map.put(ResponseService.MESSAGE, "上传失败");
        }
        String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
        assert account != null;
        account.setAvatar(uploadAvatar + name);
        String avatar = account.getAvatar();
        if (userService.updateById(account)) {
            File oldFile = new File(avatar);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            file.transferTo(new File(uploadAvatar + name));
            map.put(ResponseService.CODE, 200);
            map.put(ResponseService.SUCCESS, true);
            map.put(ResponseService.MESSAGE, "上传成功");
        }
        return map;
    }

    @GetMapping("GetAvatar")
    public void getAvatar(HttpServletResponse response, String id) throws FileNotFoundException {
        String path = userMapper.findAvatarById(id);
        System.out.println(path);
        File file = new File(path);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("SendMessage")
    public RestResponse<String> sendMessage(@RequestBody Map<String, String> map, HttpServletResponse response) throws Exception {
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
    public RestResponse<String> updatePhone(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String codeFromPhone = (String) request.getSession().getAttribute("phone-code-" + map.get("phone"));
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, map.get("phone"));
        Account checkPhoneHaveOrNot = userService.getOne(queryWrapper);

        if (checkPhoneHaveOrNot != null) {
            return RestResponse.failure(401, "手机号已被占用");
        } else if (codeFromPhone.matches(map.get("code"))) {
            Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
            assert account != null;
            account.setPhone(map.get("phone"));
            userService.updateById(account);
            return RestResponse.success("修改成功，请重新登录");
        }
        return RestResponse.failure(401, "验证码错误");
    }


    @PostMapping("SendEmail")
    public RestResponse<String> sendEmail(@RequestBody Map<String, String> map, HttpServletResponse response) throws Exception {
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
            Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
            account.setEmail(map.get("email"));
            userService.updateById(account);
            return RestResponse.success("修改成功，请重新登录");
        }
        return RestResponse.failure(401, "验证码错误");
    }

    @PostMapping("UpdatePassword")
    public RestResponse<String> updatePassword(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
        assert account != null;
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
            Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
            assert account != null;
            account.setUsername(map.get("username"));
            userService.updateById(account);
            return RestResponse.success("修改成功");
        }
    }
}
