package com.peirong.config;

import com.peirong.interceptor.AuthorizeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Resource
    AuthorizeInterceptor authorizeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(authorizeInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 不拦截的请求、注册、找回密码等请求
                // 测试阶段放行file接口的所有请求，测试完成后关闭。
                .excludePathPatterns("/before/**","/file/**","/check/**");
    }
}
