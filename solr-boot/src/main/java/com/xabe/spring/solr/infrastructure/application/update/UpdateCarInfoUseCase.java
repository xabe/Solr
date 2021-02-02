package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.CarInfoUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateCarInfoUseCase extends UpdateUseCase<UpdateCarInfoDTO> {

  @Autowired
  public UpdateCarInfoUseCase(final Logger logger,
      @Qualifier("carInfoValidate") final ValidateVehicle<UpdateCarInfoDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateCarInfoDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateCarInfoDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new CarInfoUpdater(dto));
    this.logger.debug("Car info has been updated for vehicleId: {}", dto.getId());
  }

}
