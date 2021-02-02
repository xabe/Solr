package com.xabe.spring.solr.infrastructure.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfiguration {

  @Bean
  public ExecutorService getExecutorService() {
    return Executors.newFixedThreadPool(10);
  }

}
