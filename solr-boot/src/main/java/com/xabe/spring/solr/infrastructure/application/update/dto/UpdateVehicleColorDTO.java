package com.xabe.spring.solr.infrastructure.application.update.dto;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor(force = true)
public class UpdateVehicleColorDTO extends UpdateBaseDTO {

  @Builder.Default
  private final List<ColorDTO> colors = Collections.emptyList();

}
