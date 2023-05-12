package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.sql.Date;

/**
 * @author Peirong
 */

@Data
@ToString(callSuper = true)
@TableName("user")
public class Account {

    @TableId(type = IdType.AUTO)
    @Id
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String password;
    private String avatar;
    private Date created_at;
    private Date update_time;
}
