package com.peirong.service.Impl;

import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class AuthorizeService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null)
            throw new UsernameNotFoundException("用户名不能为空");
        Account user =  userMapper.findUserByUsernameOrPhoneOrEmail(username);
        if (user == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("user")
                .build();
    }
}