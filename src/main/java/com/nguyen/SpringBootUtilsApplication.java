package com.nguyen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class SpringBootUtilsApplication implements CommandLineRunner {

    @Autowired
    private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootUtilsApplication.class, args);
	}

	@Override
	public void run(String... args) {
        log.info("Docker service started in {} environment!", env.getActiveProfiles());
    }
}
