package com.mall.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall"})
public class MallInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallInventoryApplication.class, args);
    }
}
