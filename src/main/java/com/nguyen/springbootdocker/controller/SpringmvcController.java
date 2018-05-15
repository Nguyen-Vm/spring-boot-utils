package com.nguyen.springbootdocker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RWM
 * @date 2018/3/20
 * @description: freemarker，thymeleaf，velocity模板引擎
 */
@Controller
@RequestMapping("/springmvc")
public class SpringmvcController {

    /**
     * freemarker-ftl;thymeleaf-html；
     * 同时集成freemarker,thymeleaf时，优先使用freemarker
     *
     * velocity-vm
     */
    @RequestMapping("/hello")
    public String index(Model model){
        model.addAttribute("MSG","Hello SpringMVC!");
        return "springmvc";
    }

    @GetMapping("/writer")
    public void getWriter(HttpServletResponse response) throws IOException {
        response.getWriter().write("www.baidu.com");
    }
}
