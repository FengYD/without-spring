package org.example.withoutspring.service;

import org.example.withoutspring.annotations.MyService;

/**
 * @author fengyadong
 * @date 2022/5/26 11:42
 * @Description
 */
@MyService(value = "helloService")
public class HelloService {

    public String hello(){
        return "hello3";
    }

}
