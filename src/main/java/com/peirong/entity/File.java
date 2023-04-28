package com.peirong.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Peirong
 */
@Data
@ToString(callSuper = true)
@TableName("File")
public class File {
    private Long id;
    private Long user_id;
    private Long parent_id;
    private String filename;
    private String path;
    private Long size;
    private String type;
    private Date created_at;
    private Date update_time;
}
