package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author Peirong
 */
@Mapper
public interface UserMapper extends BaseMapper<Account> {
    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone} OR email = #{email}")
    int findByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    @Select("SELECT * FROM user WHERE username = #{username} OR phone = #{username} OR email = #{username}")
    Account findUserByUsernameOrPhoneOrEmail(@Param("username") String username);

    @Update("UPDATE user SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatarById(@Param("id") Long id, @Param("avatar") String avatar);

    @Update("UPDATE user SET username = #{username} WHERE id = #{id}")
    Account updateUsernameById(@Param("id") Long id, @Param("username") String username);
}
