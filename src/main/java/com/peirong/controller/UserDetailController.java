package com.peirong.controller;

import com.peirong.entity.Account;
import com.peirong.entity.RestBeanResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;


@RestController
@RequestMapping("/api/user")
public class UserDetailController {
    @GetMapping("/me")
    public RestBeanResponse<Account> me(@SessionAttribute("account") Account account) {
        return RestBeanResponse.success(account);
    }
}