package com.peirong.controller;

import com.peirong.entity.Account;
import com.peirong.entity.RestResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class CheckUserController {
    @Resource
    private HttpServletRequest request;

    @GetMapping("/user2")
    public Map<String, String> check() {
        Map<String, String> map = new HashMap<>();
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            map.put("msg", "fail");
        } else {
            map.put("msg", "success");
        }
        return map;
    }

    @GetMapping("/user")
    public RestResponse<Account> CheckUsername(@SessionAttribute Account account) {
        return RestResponse.success(account);
    }
}