package org.spring.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author luckylau
 * @date 2019/8/10
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Redis3 {
    public static void main(String[] args) {
        SpringApplication.run(Redis3.class, args);
    }
}
