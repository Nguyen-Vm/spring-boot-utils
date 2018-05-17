package com.nguyen.dto;

/**
 * @author RWM
 * @date 2018/3/20
 * @description:
 */
public class AppException extends RuntimeException {

    public IMessageCode code;
    public Throwable cause;
    public Object data;

    public AppException(IMessageCode code){
        super(code.message());
        this.code = code;
    }

    public AppException(IMessageCode code, Throwable cause){
        super(code.message(), cause);
        this.code = code;
        this.cause = cause;
    }

    public AppException(IMessageCode code, Object data){
        super(code.message());
        this.code = code;
        this.data = data;
    }
}
