package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
@TableName("users")

public class Account {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
}
