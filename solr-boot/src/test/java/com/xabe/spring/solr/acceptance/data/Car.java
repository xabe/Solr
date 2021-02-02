package com.xabe.spring.solr.acceptance.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Car {

  private final String id;

  private final int year;

  private final String make;

  private final String model;

  private final String category;

  @JsonCreator
  public Car(@JsonProperty("objectId") final String id,
      @JsonProperty("Year") final int year,
      @JsonProperty("Make") final String make,
      @JsonProperty("Model") final String model,
      @JsonProperty("Category") final String category) {
    this.id = id;
    this.year = year;
    this.make = make;
    this.model = model;
    this.category = category;
  }
}
