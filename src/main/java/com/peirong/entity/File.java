package com.peirong.entity;



import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file")
@ApiModel(value="File对象")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String filename;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    @TableField("filePath")
    private String filepath;

    private Long owner;
    private String path;
    private Double size;
}

