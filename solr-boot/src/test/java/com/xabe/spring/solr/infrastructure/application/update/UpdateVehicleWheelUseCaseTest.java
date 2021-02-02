package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehicleWheelUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehicleWheelDTO> {

  @Override
  protected UpdateUseCase<UpdateVehicleWheelDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehicleWheelDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehicleWheelUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehicleWheelDTO createDTO() {
    return UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().type(VehicleTypeDTO.CAR).id("id").build()).storeId("storeId").build();
  }

}