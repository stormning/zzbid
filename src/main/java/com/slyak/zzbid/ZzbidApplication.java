package com.slyak.zzbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.slyak")
@EnableScheduling
@EnableAsync
public class ZzbidApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZzbidApplication.class, args);
    }
}
