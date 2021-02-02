package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WheelTypeDO {
  ALL_SEASONS,
  WINTER,
  SUMMER;

  static final Map<String, WheelTypeDO> WHEEL_TYPE_MAP =
      Stream.of(WheelTypeDO.values())
          .collect(Collectors.collectingAndThen(Collectors.toMap(WheelTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static WheelTypeDO getType(final String type) {
    return WHEEL_TYPE_MAP.get(type);
  }
}