package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WheelSizeDO {
  R_14(14),
  R_15(15),
  R_16(16),
  R_17(17),
  R_18(18),
  R_19(19),
  R_20(20),
  R_21(21),
  R_22(22);

  static final Map<Integer, WheelSizeDO> WHEEL_SIZE_MAP =
      Stream.of(WheelSizeDO.values())
          .collect(
              Collectors.collectingAndThen(
                  Collectors.toMap(WheelSizeDO::getValue, Function.identity()),
                  Collections::unmodifiableMap));

  private final Integer value;

  WheelSizeDO(final int value) {
    this.value = value;
  }

  public Integer getValue() {
    return this.value;
  }

  public static WheelSizeDO getSize(final int size) {
    return WHEEL_SIZE_MAP.getOrDefault(size, WheelSizeDO.R_16);
  }
}