package com.xabe.spring.solr.infrastructure.config;

import com.xabe.spring.solr.infrastructure.config.solr.SolrAuthInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SolrConfiguration {

  @Value("${db.vehicle.url}")
  private String solrURL;

  @Value("${db.vehicle.collection}")
  private String collection;

  @Value("${db.vehicle.user}")
  private String solrUser;

  @Value("${db.vehicle.password}")
  private String solrPassword;

  @Value("${db.vehicle.client.max-connections:25}")
  private String maxConnections;

  @Value("${db.vehicle.client.max-connections-per-host:25}")
  private String maxConnectionsPerHost;

  @Value("${db.vehicle.client.connection-timeout:100}")
  private String connectionTimeout;

  @Value("${db.vehicle.client.request-timeout:1000}")
  private String requestTimeout;

  @Value("${db.vehicle.client.allow-compression:false}")
  private String allowCompression;

  @Bean(destroyMethod = "close")
  public CloseableHttpClient getHttpClient() {
    final ModifiableSolrParams params = new ModifiableSolrParams();
    params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, this.maxConnections);
    params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, this.maxConnectionsPerHost);
    params.set(HttpClientUtil.PROP_CONNECTION_TIMEOUT, this.connectionTimeout);
    params.set(HttpClientUtil.PROP_SO_TIMEOUT, this.requestTimeout);
    params.set(HttpClientUtil.PROP_ALLOW_COMPRESSION, this.allowCompression);
    params.set(HttpClientUtil.PROP_USE_RETRY, "true");

    if (StringUtils.isNoneBlank(this.solrUser, this.solrPassword)) {
      params.set(HttpClientUtil.PROP_BASIC_AUTH_USER, this.solrUser);
      params.set(HttpClientUtil.PROP_BASIC_AUTH_PASS, this.solrPassword);
      HttpClientUtil.addRequestInterceptor(new SolrAuthInterceptor());
    }

    return HttpClientUtil.createClient(params);
  }

  @Profile("one")
  @Bean(destroyMethod = "close", name = "solrClient")
  public SolrClient getSolrClient(final CloseableHttpClient closeableHttpClient) {
    return new HttpSolrClient.Builder(this.solrURL + this.collection)
        .withSocketTimeout(Integer.parseInt(this.requestTimeout))
        .withConnectionTimeout(Integer.parseInt(this.connectionTimeout))
        .withHttpClient(closeableHttpClient)
        .withResponseParser(new BinaryResponseParser()).build();
  }

  @Profile("!one")
  @Bean(destroyMethod = "close", name = "solrClient")
  public SolrClient getSolrClient2() {
    return new Http2SolrClient.Builder(this.solrURL + this.collection)
        .idleTimeout(1000)
        .useHttp1_1(false)
        .connectionTimeout(Integer.parseInt(this.connectionTimeout))
        .maxConnectionsPerHost(Integer.parseInt(this.maxConnectionsPerHost))
        .build();
  }
}
