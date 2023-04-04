package com.peirong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peirong.entity.User;

/**
 * @author Peirong
 */
public interface UserService extends IService<User> {
    boolean saveAccount(User user);
}
