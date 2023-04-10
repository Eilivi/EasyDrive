package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author Peirong
 */

@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("User")
public class User {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String password;
    private Date created_at;
    private Date update_time;
}
