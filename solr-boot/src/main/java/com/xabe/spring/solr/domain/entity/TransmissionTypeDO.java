package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TransmissionTypeDO {
  MANUAL,
  AUTOMATIC;

  static final Map<String, TransmissionTypeDO> TRANSMISSION_TYPE_MAP =
      Stream.of(TransmissionTypeDO.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(TransmissionTypeDO::name, Function.identity()), Collections::unmodifiableMap));

  public static TransmissionTypeDO getType(final String type) {
    return TRANSMISSION_TYPE_MAP.get(type);
  }
}
