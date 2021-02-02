package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleBasicInfoUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehicleBasicInfoUseCase extends UpdateUseCase<UpdateVehicleBasicInfoDTO> {

  @Autowired
  public UpdateVehicleBasicInfoUseCase(final Logger logger,
      @Qualifier("vehicleBasicInfoValidate") final ValidateVehicle<UpdateVehicleBasicInfoDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehicleBasicInfoDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehicleBasicInfoDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehicleBasicInfoUpdater(dto));
    this.logger.debug("Vehicle basic info has been updated for vehicleId: {}", dto.getId());
  }


}
