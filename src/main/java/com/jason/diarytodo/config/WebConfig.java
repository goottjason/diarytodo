package com.jason.diarytodo.config;

import com.jason.diarytodo.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor // final로 선언된 필드들을 파라미터로 받는 생성자를 Lombok이 자동 생성
public class WebConfig implements WebMvcConfigurer {

  @Value("${file.upload-base-dir}")
  private String uploadDir;

  // AuthInterceptor를 의존성 주입 받음 (생성자를 통해 주입됨)
  private final AuthInterceptor authInterceptor;


  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/upload/**")
      .addResourceLocations("file:" + uploadDir);
  }


  @Override
  public void addInterceptors(InterceptorRegistry registry) { // 인터셉터 등록 및 관리 시 필요

    /* [[ 동작 원리 ]]
    addInterceptor() 메서드를 통해 인터셉터를 InterceptorRegistry 객체에 추가하고, 각 인터셉터가 적용될 URL 패턴을 지정
    등록된 인터셉터들은 DispatcherServlet(스프링의 중앙 서블릿)이 컨트롤러에 요청을 전달하기 전/후에 자동으로 실행
    HTTP 요청이 DispatcherServlet에 도달한 뒤, 컨트롤러에 전달되기 전에 인터셉터가 실행
    (DispatcherServlet과 컨트롤러 사이에서 동작)
     */

    registry.addInterceptor(authInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns(
        "/",
        "/member/login",
        "/member/signup",
        "/cboard",
        "/cboard/list",
        "/css/**",
        "/js/**",
        "/assets/**"
      );
  }

}

