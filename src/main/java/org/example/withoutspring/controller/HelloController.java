package org.example.withoutspring.controller;

import org.example.withoutspring.annotations.MyAutowired;
import org.example.withoutspring.annotations.MyController;
import org.example.withoutspring.annotations.MyMapping;
import org.example.withoutspring.annotations.MyRequestParam;
import org.example.withoutspring.service.HelloService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fengyadong
 * @date 2022/5/26 11:43
 * @Description
 */
@MyController
public class HelloController {

    @MyAutowired
    private HelloService helloService;

    @MyMapping(value = "/1")
    public void hello1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("Hello World1");
    }

    @MyMapping(value = "/2")
    public void hello2(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String name)throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("Hello World2" + name);
    }

    @MyMapping(value = "/3")
    public void hello3(HttpServletRequest request, HttpServletResponse response )throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(helloService.hello());
    }
}
