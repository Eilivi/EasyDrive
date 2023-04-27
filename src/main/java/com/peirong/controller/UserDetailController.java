package com.peirong.controller;

import com.peirong.entity.RestBean;
import com.peirong.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;


@RestController
@RequestMapping("/api/user")
public class UserDetailController {
    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser user) {
        return RestBean.success(user);
    }
}
