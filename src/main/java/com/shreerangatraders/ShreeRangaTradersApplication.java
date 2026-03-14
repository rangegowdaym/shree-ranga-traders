package com.shreerangatraders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ShreeRangaTradersApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShreeRangaTradersApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ShreeRangaTradersApplication.class, args);
    }
}
