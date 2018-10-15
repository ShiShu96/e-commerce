package com.xy.ecommerce;

import com.xy.ecommerce.controller.RequstLogInteceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequstLogInteceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
