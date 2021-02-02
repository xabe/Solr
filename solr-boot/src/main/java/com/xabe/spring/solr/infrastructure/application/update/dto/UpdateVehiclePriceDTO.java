package com.xabe.spring.solr.infrastructure.application.update.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor(force = true)
public class UpdateVehiclePriceDTO extends UpdateBaseDTO {

  private final PricesDTO prices;

}
