package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("file")
public class Files implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;
    private String filename;
    private String filepath;
    private Double filesize;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String upload_time;
    private String folder;
}