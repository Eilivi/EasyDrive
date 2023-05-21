package com.peirong.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import com.peirong.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class CheckUser {
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    @GetMapping("/user")
    public Map<String, String> check(String username) {
        Map<String, String> map = new HashMap<>();
        Long id = userMapper.findIdByUsernameOrPhoneOrEmail(username);
        Account account = (Account) redisTemplate.opsForValue().get("account_" + id.toString());
        request.getSession().setAttribute("account", account);
        if (account == null) {
            map.put("msg", "fail");
        } else {
            map.put("msg", "success");
            map.put("id", String.valueOf(account.getId()));
            map.put("username", account.getUsername());
            map.put("phone", account.getPhone());
            map.put("email", account.getEmail());
        }
        return map;
    }

    @GetMapping("/auth")
    public Map<String, String> check() {
        Map<String, String> map = new HashMap<>();
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        if (account == null) {
            map.put("msg", "fail");
        } else {
            map.put("msg", "success");
        }
        return map;
    }
}