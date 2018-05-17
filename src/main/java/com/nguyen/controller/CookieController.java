package com.nguyen.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RWM
 * @date 2018/3/19
 * @description:
 */
@RestController
@RequestMapping("/cookie")
public class CookieController {

    private static final String COOKIE_DOMAIN = "127.0.0.1";
    private static final String COOKIE_NAME = "OPEN_ID";

    @RequestMapping("/hello")
    public String index(){
        return "Hello cookie!";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        writeLoginToken(response, "OPEN_ID");
        return "login success";
    }

    @GetMapping("/relogin")
    public String relogin(HttpServletRequest request, HttpServletResponse response) {
        return readLoginToken(request);
    }

    @GetMapping("/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        delInvalidCookie(request, response);
    }

    /** 读取Cookie **/
    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if (cks != null){
            for (Cookie ck : cks){
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)){
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    /** 写入Cookie **/
    public static void writeLoginToken(HttpServletResponse httpServletResponse, String openId){
        Cookie ck = new Cookie(COOKIE_NAME, openId);
        ck.setDomain(COOKIE_DOMAIN);
        //代表设置在根目录
        ck.setPath("/");

        //单位是秒
        //如果这个MaxAge不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        //如果是-1，代表永久
        ck.setMaxAge(60 * 60 * 24 * 365);
        httpServletResponse.addCookie(ck);
    }

    /** 删除Cookie **/
    private void delInvalidCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null){
            for (Cookie ck : cks){
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");

                    //设置成0，代表删除此Cookie
                    ck.setMaxAge(0);
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
