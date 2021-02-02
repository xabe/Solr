package com.xabe.spring.solr.infrastructure.config.solr;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor is added to add basic authentication in header. SolrJ is not able to sent authentication in header for update and add
 * request, so need ti implement this interceptor to add authentication header in solr request.
 */
public class SolrAuthInterceptor implements HttpRequestInterceptor {

  private final Logger logger;

  private ContextAwareAuthScheme authScheme;

  public SolrAuthInterceptor() {
    this.logger = LoggerFactory.getLogger(SolrAuthInterceptor.class);
    this.authScheme = new BasicScheme();
  }

  @Override
  public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
    final AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

    if (authState != null && authState.getAuthScheme() == null) {
      final CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
      final HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
      final Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
      if (creds == null) {
        throw new HttpException("No credentials for preemptive authentication");
      }
      request.addHeader(this.authScheme.authenticate(creds, request, context));
    } else {
      this.logger.debug("authState is null. No preemptive authentication.");
    }
  }

  public ContextAwareAuthScheme getAuthScheme() {
    return this.authScheme;
  }

  public void setAuthScheme(final ContextAwareAuthScheme authScheme) {
    this.authScheme = authScheme;
  }

}

