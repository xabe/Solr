package com.xabe.spring.solr.infrastructure.application.search.dto;

import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true)
public abstract class VehicleDTO {

  private final VehicleIdDTO vehicleId;

  private final List<String> category;

  private final String brand;

  private final String model;

  private List<ColorDTO> colors;

  private List<EngineDTO> engines;

  private PricesDTO prices;

  private List<String> transmissions;

  private List<WheelDTO> wheels;

  public boolean isCar() {
    return this instanceof CarDTO;
  }
}
