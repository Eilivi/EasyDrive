package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * @author Peirong
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    @Select("SELECT * FROM file WHERE filename = #{filename}")
    File findByFilename(String filename);

    @Select("SELECT * FROM file WHERE id = #{id}")
    File findById(Long id);

    @Select("SELECT * FROM file WHERE path = #{path}")
    File findByPath(String path);

    @Select("SELECT * FROM file WHERE owner = #{owner}")
    File findByOwner(Long owner);

    @Select("SELECT * FROM file WHERE filepath = #{filepath}")
    File findByFilepath(String filepath);

}
