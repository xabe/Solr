package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleTransmissionUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehicleTransmissionUseCase extends UpdateUseCase<UpdateVehicleTransmissionDTO> {

  @Autowired
  public UpdateVehicleTransmissionUseCase(final Logger logger,
      @Qualifier("vehicleTransmissionValidate") final ValidateVehicle<UpdateVehicleTransmissionDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehicleTransmissionDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehicleTransmissionDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehicleTransmissionUpdater(dto));
    this.logger.debug("Vehicle transmission has been updated for vehicleId: {}", dto.getId());
  }

}
