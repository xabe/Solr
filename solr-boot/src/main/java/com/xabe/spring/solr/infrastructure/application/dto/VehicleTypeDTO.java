package com.xabe.spring.solr.infrastructure.application.dto;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum VehicleTypeDTO {
  UNSPECIFIED,
  CAR,
  TRUCK;

  public static final Map<String, VehicleTypeDTO> TYPE_MAP = Stream.of(VehicleTypeDTO.values()).collect(Collectors.collectingAndThen(
      Collectors.toMap(VehicleTypeDTO::name, Function.identity()), Collections::unmodifiableMap));

  public static VehicleTypeDTO getVehicleType(final String value) {
    return TYPE_MAP.getOrDefault(value.toUpperCase(), VehicleTypeDTO.UNSPECIFIED);
  }
}
