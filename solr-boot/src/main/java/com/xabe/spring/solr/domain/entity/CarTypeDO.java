package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CarTypeDO {
  SEDAN,
  COUPE,
  HATCHBACK,
  VAN,
  STATION_WAGON,
  CONVERTIBLE,
  SUV,
  PICK_UP;

  static final Map<String, CarTypeDO> CAR_TYPE_MAP =
      Stream.of(CarTypeDO.values())
          .collect(Collectors.collectingAndThen(Collectors.toMap(CarTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static CarTypeDO getType(final String type) {
    return CAR_TYPE_MAP.get(type);
  }
}
