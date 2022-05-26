package org.example.withoutspring.mysql;

import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author fengyadong
 * @date 2022/5/26 16:04
 * @Description
 */
public interface UserMapper {

    List<User> selectAllUsers();

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectUser(Long id);

}
