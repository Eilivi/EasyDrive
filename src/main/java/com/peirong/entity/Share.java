package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("share")
public class Share implements Serializable {
    private String id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileId;
    private String expireTime;
}
