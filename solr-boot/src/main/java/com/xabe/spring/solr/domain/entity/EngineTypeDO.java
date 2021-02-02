package com.xabe.spring.solr.domain.entity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EngineTypeDO {
  UNSPECIFIED,
  IN_LINE,
  V,
  W,
  FLAT;

  static Map<String, EngineTypeDO> MAP_ENGINE_TYPE =
      Stream.of(EngineTypeDO.values()).collect(Collectors.toMap(EngineTypeDO::name, Function.identity()));

  public static EngineTypeDO value(final String value) {
    return MAP_ENGINE_TYPE.getOrDefault(value, UNSPECIFIED);
  }
}
