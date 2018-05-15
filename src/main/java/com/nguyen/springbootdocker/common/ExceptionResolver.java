package com.nguyen.springbootdocker.common;

import com.nguyen.springbootdocker.dto.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object o,
                                         Exception e) {
        log.error("{} Exception", request.getRequestURI(), e);
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("status", e instanceof AppException ? ((AppException) e).code.code(): ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg", e instanceof AppException ? ((AppException) e).code.msg() : "接口异常，详情请查看服务端日志的信息");
        return modelAndView;
    }
}
