package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehicleColorUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehicleColorDTO> {

  @Override
  protected UpdateUseCase<UpdateVehicleColorDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehicleColorDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehicleColorUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehicleColorDTO createDTO() {
    return UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").build();
  }
}