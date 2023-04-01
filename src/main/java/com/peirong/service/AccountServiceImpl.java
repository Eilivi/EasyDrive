package com.peirong.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.User;
import com.peirong.mapper.AccountMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, User> implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    public boolean save(User user) {
        /** 检查该账户是否已存在*/

        int count = findByPhoneOrEmail(user.getPhone(), user.getEmail());
        if (count > 0) {
            return false;
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        int result = accountMapper.insert(user);
        return result > 0;
    }

    public int findByPhoneOrEmail(String phone, String email) {
        return accountMapper.findByPhoneOrEmail(phone, email);
    }
}



/**在注册时就可以同时检查手机号码和邮箱，
 * 如果其中一个已经被注册过，
 * 就返回失败，
 * 否则进行密码哈希处理，
 * 并将账户信息保存到数据库中。*/