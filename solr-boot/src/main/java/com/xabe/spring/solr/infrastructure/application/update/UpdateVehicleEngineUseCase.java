package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleEngineUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehicleEngineUseCase extends UpdateUseCase<UpdateVehicleEngineDTO> {

  @Autowired
  public UpdateVehicleEngineUseCase(final Logger logger,
      @Qualifier("vehicleEngineValidate") final ValidateVehicle<UpdateVehicleEngineDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehicleEngineDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehicleEngineDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehicleEngineUpdater(dto));
    this.logger.debug("Vehicle engine has been updated for vehicleId: {}", dto.getId());
  }


}
