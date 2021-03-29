package com.tripaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication(scanBasePackages = {"com.tripaction"})
public class RateLimiterApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RateLimiterApplication.class, args);
    }
}