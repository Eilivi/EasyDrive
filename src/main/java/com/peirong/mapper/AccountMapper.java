package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM account WHERE phone = #{phone} OR email = #{email}")
    int findByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

}
