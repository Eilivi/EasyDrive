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
    public boolean saveAccount(Account account) {

        //System.out.println(user);
        int count = findByPhoneOrEmail(account.getPhone(), account.getEmail());
        if (count > 0) {
            return false;
        }
        account.setPassword(encoder.encode(account.getPassword()));
        int result = userMapper.insert(account);
        return result > 0;
    }

    @Override
    public Account updateAvatar(Account user) {
        //user.setAvatar();
        return null;
    }
    public int findByPhoneOrEmail(String phone, String email) {
        return userMapper.findByPhoneOrEmail(phone, email);
    }
    public void updateAvatar(Long id, String avatar) {
        int rows = userMapper.updateAvatarById(id, avatar);
        if (rows <= 0) {
            throw new RuntimeException("更新用户头像失败");
        }
    }
}