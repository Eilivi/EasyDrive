package com.peirong.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.Account;
import com.peirong.mapper.AccountMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
}
