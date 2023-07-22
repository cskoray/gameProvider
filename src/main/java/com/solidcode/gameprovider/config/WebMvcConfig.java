package com.solidcode.gameprovider.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  AppMvcInterceptor appMvcInterceptor;

  @Autowired
  public WebMvcConfig(AppMvcInterceptor appMvcInterceptor) {
    this.appMvcInterceptor = appMvcInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(appMvcInterceptor);
  }
}