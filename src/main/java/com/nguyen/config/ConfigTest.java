package com.nguyen.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by RWM on 2019/3/22
 **/
public class ConfigTest {

    public static void main(String[] args) {
        Config config = ConfigFactory.load("application.conf");
        System.out.println(config.getString("complex-app.something"));
        System.out.println(config.getString("simple-lib.foo"));
        System.out.println(config.getString("simple-lib.whatever"));
    }
}
