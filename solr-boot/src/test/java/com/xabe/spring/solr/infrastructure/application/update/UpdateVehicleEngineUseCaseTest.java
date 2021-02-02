package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehicleEngineUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehicleEngineDTO> {

  @Override
  protected UpdateUseCase<UpdateVehicleEngineDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehicleEngineDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehicleEngineUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehicleEngineDTO createDTO() {
    return UpdateVehicleEngineDTO.builder().id(VehicleIdDTO.builder().type(VehicleTypeDTO.CAR).id("id").build()).storeId("storeId").build();
  }
}