package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehicleTransmissionUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehicleTransmissionDTO> {

  @Override
  protected UpdateUseCase<UpdateVehicleTransmissionDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehicleTransmissionDTO> validateVehicle, final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehicleTransmissionUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehicleTransmissionDTO createDTO() {
    return UpdateVehicleTransmissionDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId")
        .build();
  }
}