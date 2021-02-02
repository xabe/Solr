package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.slf4j.Logger;

class UpdateVehiclePriceUseCaseTest extends UpdateVehicleBaseUseCaseTest<UpdateVehiclePriceDTO> {

  @Override
  protected UpdateUseCase<UpdateVehiclePriceDTO> createUseCase(final Logger logger,
      final ValidateVehicle<UpdateVehiclePriceDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    return new UpdateVehiclePriceUseCase(logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected UpdateVehiclePriceDTO createDTO() {
    return UpdateVehiclePriceDTO.builder().id(VehicleIdDTO.builder().type(VehicleTypeDTO.CAR).id("id").build()).storeId("storeId").build();
  }
}