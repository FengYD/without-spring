package org.example.withoutspring.controller;

import org.example.withoutspring.annotations.MyAutowired;
import org.example.withoutspring.annotations.MyController;
import org.example.withoutspring.annotations.MyMapping;
import org.example.withoutspring.annotations.MyRequestParam;
import org.example.withoutspring.redis.RedisTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fengyadong
 * @date 2022/5/26 15:03
 * @Description
 */
@MyController
public class RedisController {

    @MyAutowired
    private RedisTemplate redisTemplate;


    @MyMapping(value = "/redis/get")
    public void get(HttpServletRequest request, HttpServletResponse response,
                       @MyRequestParam("key") String key) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("redis get:" + "key = " + key + " value = " + redisTemplate.get(key));
    }

    @MyMapping(value = "/redis/set")
    public void set(HttpServletRequest request, HttpServletResponse response,
                       @MyRequestParam("key") String key, @MyRequestParam("value") String value) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        redisTemplate.set(key, value);
        response.getWriter().write("redis set:" + "key = " + key + " value = " + value);
    }

}
