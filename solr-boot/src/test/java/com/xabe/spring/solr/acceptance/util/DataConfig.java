package com.xabe.spring.solr.acceptance.util;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

  @Bean
  public LoadData getLoadData(final SolrClient solrClient) {
    return new LoadData(solrClient);
  }

}
