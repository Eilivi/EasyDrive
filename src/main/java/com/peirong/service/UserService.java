package com.peirong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peirong.entity.Account;

public interface UserService extends IService<Account> {
    String saveAccount(Account account);
}
