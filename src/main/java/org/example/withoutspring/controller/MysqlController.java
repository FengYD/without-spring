package org.example.withoutspring.controller;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.session.SqlSession;
import org.example.withoutspring.annotations.MyController;
import org.example.withoutspring.annotations.MyMapping;
import org.example.withoutspring.mysql.MyBatisUtils;
import org.example.withoutspring.mysql.User;
import org.example.withoutspring.mysql.UserMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author fengyadong
 * @date 2022/5/26 16:06
 * @Description
 */
@MyController
public class MysqlController {

    @MyMapping(value = "/mysql/user")
    public void user(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        String jsonString = "";
        try (SqlSession session = MyBatisUtils.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectUser(1L);
            jsonString = JSON.toJSONString(user);
        }
        response.getWriter().write(jsonString);
    }

    @MyMapping(value = "/mysql/allUser")
    public void allUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        String jsonString = "";
        try (SqlSession session = MyBatisUtils.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> user = mapper.selectAllUsers();
            jsonString = JSON.toJSONString(user);
        }
        response.getWriter().write(jsonString);
    }

}
