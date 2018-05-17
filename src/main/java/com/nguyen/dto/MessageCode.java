package com.nguyen.dto;

/**
 * @author RWM
 * @date 2018/3/20
 * @description:
 */
public enum MessageCode implements IMessageCode {

    GlobalException("D00001","全局异常处理");

    private final String code;
    private final String desc;

    MessageCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String msg() {
        return desc;
    }
}
