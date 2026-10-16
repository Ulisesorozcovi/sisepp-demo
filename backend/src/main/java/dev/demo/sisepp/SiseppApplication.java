package dev.demo.sisepp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SiseppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SiseppApplication.class, args);
    }
}
