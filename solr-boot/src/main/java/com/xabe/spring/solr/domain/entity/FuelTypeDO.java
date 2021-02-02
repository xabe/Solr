package com.xabe.spring.solr.domain.entity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FuelTypeDO {
  UNSPECIFIED,
  PETROL,
  ELECTRIC,
  HYDROGEN;

  static Map<String, FuelTypeDO> MAP_FUEL_TYPE =
      Stream.of(FuelTypeDO.values()).collect(Collectors.toMap(FuelTypeDO::name, Function.identity()));

  public static FuelTypeDO value(final String value) {
    return MAP_FUEL_TYPE.getOrDefault(value, UNSPECIFIED);
  }
}
