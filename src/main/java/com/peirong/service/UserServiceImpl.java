package com.peirong.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.User;
import com.peirong.mapper.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Peirong
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean saveAccount(User user) {

        int count = findByPhoneOrEmail(user.getPhone(), user.getEmail());
        if (count > 0) {
            return false;
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        int result = userMapper.insert(user);
        return result > 0;
    }

    public int findByPhoneOrEmail(String phone, String email) {
        return userMapper.findByPhoneOrEmail(phone, email);
    }
}