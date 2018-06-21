package com.nguyen.dto;

/**
 * @author RWM
 * @date 2018/6/21
 */
public class RabbitMessage {
    public String name;
    public String phone;
    public Integer age;

    @Override
    public String toString() {
        return "RabbitMessage{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                '}';
    }
}
