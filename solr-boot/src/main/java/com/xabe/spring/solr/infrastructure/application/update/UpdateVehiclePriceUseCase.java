package com.xabe.spring.solr.infrastructure.application.update;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehiclePriceUpdater;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateVehiclePriceUseCase extends UpdateUseCase<UpdateVehiclePriceDTO> {

  @Autowired
  public UpdateVehiclePriceUseCase(final Logger logger,
      @Qualifier("vehiclePriceValidate") final ValidateVehicle<UpdateVehiclePriceDTO> validateVehicle,
      final VehicleUpdateService vehicleUpdateService) {
    super(UpdateVehiclePriceDTO.class, logger, validateVehicle, vehicleUpdateService);
  }

  @Override
  protected void doUpdate(final UpdateVehiclePriceDTO dto) {
    this.validator.validate(dto);
    this.vehicleUpdateService.update(this.mapToId(dto.getId()), dto.getStoreId(), new VehiclePriceUpdater(dto));
    this.logger.debug("Vehicle prices has been updated for vehicleId: {}", dto.getId());
  }


}
