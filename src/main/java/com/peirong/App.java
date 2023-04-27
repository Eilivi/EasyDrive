package com.peirong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Peirong
 */
@SpringBootApplication(scanBasePackages = "com.peirong")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}