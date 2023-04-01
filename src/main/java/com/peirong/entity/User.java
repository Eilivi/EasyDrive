package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@TableName("users")
public class User {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String password;
}
