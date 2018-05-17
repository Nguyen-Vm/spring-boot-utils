package com.nguyen.dto;

/**
 * @author RWM
 * @date 2018/3/20
 */
public interface IMessageCode {

    String code();

    String msg();

    default String message(){
        return code() + "->" + msg();
    }
}
