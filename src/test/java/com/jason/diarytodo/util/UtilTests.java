package com.jason.diarytodo.util;

import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class UtilTests {
  @Autowired
  private ApiSearchBlog apiSearchBlog;

  @Test
  public void testApiSearchBlog() {
    apiSearchBlog.callSearchBlog();
  }
}
