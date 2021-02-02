package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateTruckInfoUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateTruckInfoDTO> {

  @Override
  protected UpdateUseCase<UpdateTruckInfoDTO> createUseCase(final Logger logger, final ValidateVehicle<UpdateTruckInfoDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateTruckInfoUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateTruckInfoDTO createDTO() {
    return UpdateTruckInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").build();
  }
}