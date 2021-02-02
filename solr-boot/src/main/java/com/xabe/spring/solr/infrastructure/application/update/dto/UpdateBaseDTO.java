package com.xabe.spring.solr.infrastructure.application.update.dto;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor(force = true)
public abstract class UpdateBaseDTO implements Serializable {

  private final VehicleIdDTO id;

  private final Long timestamp;

  private final String storeId;

}
