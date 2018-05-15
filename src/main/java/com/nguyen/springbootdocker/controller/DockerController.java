package com.nguyen.springbootdocker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RWM
 * @date 2018/3/19
 * @description:
 */
@RestController
@RequestMapping("/docker")
public class DockerController {

    @RequestMapping("/hello")
    public String index(){
        return "Hello Docker!";
    }
}
