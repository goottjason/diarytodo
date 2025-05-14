package com.jason.diarytodo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }


  /*
  자동 등록: 클래스에 @Component, @Service, @Repository, @Controller 등의 어노테이션을 붙이면, 스프링이 자동으로 빈으로 등록.

  수동 등록: 설정 클래스에 @Configuration을 붙이고, 메서드에 @Bean을 붙여 반환하는 객체를 빈으로 등록. 이때 메서드 이름이 빈 이름이 됨.
   */
}