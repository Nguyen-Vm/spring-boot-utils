package com.nguyen.springbootdocker.controller;

import com.nguyen.springbootdocker.dto.AppException;
import com.nguyen.springbootdocker.dto.MessageCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
@Controller
@RequestMapping("/exception")
public class ExceptionController {

    @RequestMapping("/global")
    public void exception(){
        if (true){
            throw new AppException(MessageCode.GlobalException);
        }
    }
}
