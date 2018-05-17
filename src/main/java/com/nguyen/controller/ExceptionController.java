package com.nguyen.controller;

import com.nguyen.dto.AppException;
import com.nguyen.dto.MessageCode;
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
