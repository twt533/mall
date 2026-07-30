package com.mall.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall"})
public class MallDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallDataApplication.class, args);
    }
}
