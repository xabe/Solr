package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TruckTypeDO {
  CEMENT,
  CRANE,
  FIRE,
  TOWN,
  LIVESTOCK,
  TANKERS,
  TIPPER,
  TRAILER;

  public static final Map<String, TruckTypeDO> TRUCK_TYPE_MAP = Stream.of(TruckTypeDO.values()).collect(Collectors.collectingAndThen(
      Collectors.toMap(TruckTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static TruckTypeDO getType(final String type) {
    return TRUCK_TYPE_MAP.get(type);
  }
}
