package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * @author Peirong
 */
@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {

    @Select("SELECT * FROM file WHERE filename = #{filename}")
    FileEntity findByFilename(String filename);

    @Select("SELECT * FROM file WHERE id = #{id}")
    FileEntity findById(Long id);

    @Select("SELECT * FROM file WHERE path = #{path}")
    FileEntity findByPath(String path);

    @Select("SELECT * FROM file WHERE owner = #{owner}")
    FileEntity findByOwner(Long owner);

    @Select("SELECT * FROM file WHERE filepath = #{filepath}")
    FileEntity findByFilepath(String filepath);

}
