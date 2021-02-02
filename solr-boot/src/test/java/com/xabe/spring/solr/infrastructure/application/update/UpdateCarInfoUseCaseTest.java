package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateCarInfoUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateCarInfoDTO> {

  @Override
  protected UpdateUseCase<UpdateCarInfoDTO> createUseCase(final Logger logger, final ValidateVehicle<UpdateCarInfoDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateCarInfoUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateCarInfoDTO createDTO() {
    return UpdateCarInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").build();
  }
}