package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum VehicleTypeDO {
  CAR,
  TRUCK;

  public static final Map<String, VehicleTypeDO> VEHICLE_TYPE_MAP = Stream.of(VehicleTypeDO.values()).collect(Collectors.collectingAndThen(
      Collectors.toMap(VehicleTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static VehicleTypeDO getType(final String name) {
    return VEHICLE_TYPE_MAP.getOrDefault(name, CAR);
  }
}
