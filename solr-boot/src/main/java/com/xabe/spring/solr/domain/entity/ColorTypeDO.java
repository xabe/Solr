package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ColorTypeDO {
  SOLID,
  METALLIC;

  static final Map<String, ColorTypeDO> COLOR_TYPE_MAP =
      Stream.of(ColorTypeDO.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(ColorTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static ColorTypeDO getType(final String type) {
    return COLOR_TYPE_MAP.get(type);
  }
}
