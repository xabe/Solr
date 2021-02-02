package com.xabe.spring.solr.acceptance.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandlers<T> implements BodyHandler<T> {

  private final ObjectMapper objectMapper;

  private final BodySubscriber<String> upstream;

  private final TypeReference<T> typeReference;

  public JsonBodyHandlers(final TypeReference<T> typeReference) {
    this.typeReference = typeReference;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.registerModule(new Jdk8Module());
    this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    this.objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    this.upstream = BodySubscribers.ofString(StandardCharsets.UTF_8);
  }

  @Override
  public BodySubscriber<T> apply(final ResponseInfo responseInfo) {
    return BodySubscribers.mapping(this.upstream, (body) -> {
      try {
        return this.objectMapper.readValue(body, this.typeReference);
      } catch (final IOException var3) {
        throw new UncheckedIOException(var3);
      }
    });
  }
}
