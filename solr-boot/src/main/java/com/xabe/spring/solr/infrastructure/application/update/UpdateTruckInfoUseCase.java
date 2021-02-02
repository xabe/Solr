package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.TruckInfoUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateTruckInfoUseCase extends UpdateUseCase<UpdateTruckInfoDTO> {

  @Autowired
  public UpdateTruckInfoUseCase(final Logger logger,
      @Qualifier("truckInfoValidate") final ValidateVehicle<UpdateTruckInfoDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateTruckInfoDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateTruckInfoDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new TruckInfoUpdater(dto));
    this.logger.debug("Truck info has been updated for vehicleId: {}", dto.getId());
  }

}
