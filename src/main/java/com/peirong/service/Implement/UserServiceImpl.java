package com.peirong.service.Implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import com.peirong.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author Peirong
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Account> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    BCryptPasswordEncoder encoder;
    public String saveAccount(Account account) {

        int count = findByPhoneOrEmail(account.getPhone(), account.getEmail());
        if (count > 0) {
            return "该手机号或邮箱已被注册";
        }
        account.setPassword(encoder.encode(account.getPassword()));
        int result = userMapper.insert(account);
        return result > 0 ? "注册成功" : "注册失败";
    }
    public int findByPhoneOrEmail(String phone, String email) {
        return userMapper.findByPhoneOrEmail(phone, email);
    }
}