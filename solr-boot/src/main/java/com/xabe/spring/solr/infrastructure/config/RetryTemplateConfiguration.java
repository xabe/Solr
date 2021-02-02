package com.xabe.spring.solr.infrastructure.config;

import com.xabe.spring.solr.domain.exception.VehicleConcurrentUpdateException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryTemplateConfiguration {

  @Value("${optimistic-locking.max-attemps}")
  private int maxAttemps;

  @Value("${optimistic-locking.back-off-ms}")
  private int backOffMs;

  @Bean("retryTemplateSolr")
  public RetryTemplate retryTemplate() {
    final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(this.maxAttemps,
        Collections.singletonMap(VehicleConcurrentUpdateException.class, Boolean.TRUE));

    final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(this.backOffMs);

    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(retryPolicy);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    return retryTemplate;
  }

}
