package com.dong.picture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.dong.picture.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class DongPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DongPictureBackendApplication.class, args);
    }

}
