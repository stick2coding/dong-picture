package com.dong.picture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域设置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 跨域设置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        //覆盖请求
        registry.addMapping("/**")
                .allowCredentials(true) // 允许发送cookies
                .allowedOriginPatterns("*") // 允许跨域访问的域名
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

}
