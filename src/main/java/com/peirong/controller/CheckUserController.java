package com.peirong.controller;

import com.peirong.entity.Account;
import com.peirong.entity.RestBeanResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("check")
public class CheckUserController {
    @GetMapping("user")
    public RestBeanResponse<Account> CheckLogin(@SessionAttribute("account") Account account) {
        return RestBeanResponse.success(account);
    }
}