package com.xabe.spring.solr.acceptance.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;

@Value
public class CountResponse {

  private final List<String> results;

  private final Integer count;

  @JsonCreator
  public CountResponse(@JsonProperty("results") final List<String> results, @JsonProperty("count") final Integer count) {
    this.results = results;
    this.count = count;
  }
}
