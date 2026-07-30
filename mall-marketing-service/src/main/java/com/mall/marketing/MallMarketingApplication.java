package com.mall.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall"})
public class MallMarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMarketingApplication.class, args);
    }
}
