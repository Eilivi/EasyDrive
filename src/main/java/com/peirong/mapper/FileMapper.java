package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.Files;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author Peirong
 */
@Mapper
public interface FileMapper extends BaseMapper<Files> {

    @Select("SELECT * FROM file WHERE filename = #{filename}")
    Files findByFilename(String filename);

    @Select("SELECT * FROM file WHERE id = #{id}")
    Files findById(Long id);

    @Select("SELECT * FROM file WHERE path = #{path}")
    Files findByPath(String path);

    @Select("SELECT * FROM file WHERE uid = #{owner}")
    Files findByOwner(Long owner);
}
