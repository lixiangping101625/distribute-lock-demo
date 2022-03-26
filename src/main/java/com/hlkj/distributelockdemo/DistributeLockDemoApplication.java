package com.hlkj.distributelockdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DistributeLockDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributeLockDemoApplication.class, args);
    }

}
