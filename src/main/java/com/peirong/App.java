package com.peirong;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Peirong
 */
@EnableScheduling
@MapperScan("com.peirong.mapper")
@SpringBootApplication(scanBasePackages = "com.peirong")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}