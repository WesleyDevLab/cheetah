package com.zhaijiong.analyze;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-19.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.zhaijiong.analyze")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}