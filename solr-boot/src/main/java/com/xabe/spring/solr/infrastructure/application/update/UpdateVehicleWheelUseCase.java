package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleWheelUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehicleWheelUseCase extends UpdateUseCase<UpdateVehicleWheelDTO> {

  @Autowired
  public UpdateVehicleWheelUseCase(final Logger logger,
      @Qualifier("vehicleWheelValidate") final ValidateVehicle<UpdateVehicleWheelDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehicleWheelDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehicleWheelDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehicleWheelUpdater(dto));
    this.logger.debug("Vehicle wheels has been updated for vehicleId: {}", dto.getId());
  }

}
