package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class VehicleEngineValidate extends ValidateVehicle<UpdateVehicleEngineDTO> {

  private static final String UPDATE_VEHICLE_ENGINE = "UpdateVehicleEngine";

  private static final String ENGINES = "engines";

  private static final String TYPE = "type";

  private static final String FUEL = "fuel";

  public VehicleEngineValidate() {
    super(UPDATE_VEHICLE_ENGINE);
  }

  @Override
  public void validateUpdate(final UpdateVehicleEngineDTO dto) {
    if (CollectionUtils.isEmpty(dto.getEngines())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_ENGINE, ENGINES));
    }
    dto.getEngines().forEach(this::validateEngine);
  }

  private void validateEngine(final EngineDTO engine) {
    this.validateString(engine.getType(), UPDATE_VEHICLE_ENGINE, TYPE);
    this.validateString(engine.getFuel(), UPDATE_VEHICLE_ENGINE, FUEL);
  }
}
