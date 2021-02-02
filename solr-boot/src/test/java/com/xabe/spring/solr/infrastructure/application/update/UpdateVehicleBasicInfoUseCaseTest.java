package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehicleBasicInfoUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehicleBasicInfoDTO> {

  @Override
  protected UpdateUseCase<UpdateVehicleBasicInfoDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehicleBasicInfoDTO> validateVehicle, final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehicleBasicInfoUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehicleBasicInfoDTO createDTO() {
    return UpdateVehicleBasicInfoDTO.builder().id(VehicleIdDTO.builder().type(VehicleTypeDTO.CAR).id("id").build()).storeId("storeId")
        .build();
  }

}