package com.dong.picture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.dong.picture.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync//支持异步执行方法
public class DongPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DongPictureBackendApplication.class, args);
    }

}
