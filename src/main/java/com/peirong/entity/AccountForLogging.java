package com.peirong.entity;


import lombok.Data;

@Data
public class AccountForLogging {
    Long id;
    String username;
    String phone;
    String email;
    String avatar;
}
