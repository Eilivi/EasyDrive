package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("recycle")
public class Recycle {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;
    private String filename;
    private String filepath;
    private Double filesize;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String uploadTime;
    private String folder;
}
