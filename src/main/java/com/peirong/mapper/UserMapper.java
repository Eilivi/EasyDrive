package com.peirong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peirong.entity.Account;
import com.peirong.entity.user.AccountUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author Peirong
 */
@Mapper
public interface UserMapper extends BaseMapper<Account> {
    @Select("SELECT COUNT(*) FROM User WHERE phone = #{phone} OR email = #{email}")
    int findByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    @Select("SELECT * FROM User WHERE username = #{text} OR phone = #{text} OR email = #{text}")
    Account findUserByUsernameOrPhoneOrEmail(String text);

    @Select("SELECT * FROM User WHERE username = #{text} OR phone = #{text} OR email = #{text}")
    AccountUser findAccountUserByNameOrEmailOrPhone(String text);

    @Update("UPDATE User SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatarById(@Param("id") Long id, @Param("avatar") String avatar);

}
