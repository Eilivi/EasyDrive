package com.peirong.controller;

import com.peirong.entity.RestBeanResponse;
import com.peirong.entity.AccountForLogging;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;


@RestController
@RequestMapping("/api/user")
public class UserDetailController {
    @GetMapping("/me")
    public RestBeanResponse<AccountForLogging> me(@SessionAttribute("account") AccountForLogging account) {
        return RestBeanResponse.success(account);
    }
}
