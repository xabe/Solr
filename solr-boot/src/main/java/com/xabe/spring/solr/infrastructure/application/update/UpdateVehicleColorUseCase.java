package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleColorUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehicleColorUseCase extends UpdateUseCase<UpdateVehicleColorDTO> {

  @Autowired
  public UpdateVehicleColorUseCase(final Logger logger,
      @Qualifier("vehicleColorValidate") final ValidateVehicle<UpdateVehicleColorDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehicleColorDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehicleColorDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehicleColorUpdater(dto));
    this.logger.debug("Vehicle color has been updated for vehicleId: {}", dto.getId());
  }

}
