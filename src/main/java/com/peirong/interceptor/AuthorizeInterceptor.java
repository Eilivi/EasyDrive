package com.peirong.interceptor;

import com.peirong.entity.Account;
import com.peirong.mapper.UserMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper mapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User)authentication.getPrincipal();
        String username = user.getUsername();
        Account account = mapper.findUserByUsernameOrPhoneOrEmail(username);
        request.getSession().setAttribute("account", account);
        return true;
    }
}