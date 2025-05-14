package com.jason.diarytodo.config;

import com.jason.diarytodo.interceptor.AuthInterceptor;
import com.jason.diarytodo.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {



  private final LoginInterceptor loginInterceptor;
  private final AuthInterceptor authInterceptor;



  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // WebMvcConfigurer.super.addInterceptors(registry);
    // 로그인 인터셉터
    registry.addInterceptor(loginInterceptor)
      .addPathPatterns("/member/login");
    registry.addInterceptor(authInterceptor)
      .addPathPatterns("/board/register");
  }

}

