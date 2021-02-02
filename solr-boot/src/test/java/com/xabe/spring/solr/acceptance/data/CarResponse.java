package com.xabe.spring.solr.acceptance.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;

@Value
public class CarResponse {

  private final List<Car> cars;

  @JsonCreator
  public CarResponse(@JsonProperty("results") final List<Car> cars) {
    this.cars = cars;
  }
}
