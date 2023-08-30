package kr.kdev.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AmqpApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmqpApplication.class, args);
    }
}
