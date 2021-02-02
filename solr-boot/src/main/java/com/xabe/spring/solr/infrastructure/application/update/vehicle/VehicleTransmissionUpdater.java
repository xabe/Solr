package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import java.util.stream.Collectors;

public class VehicleTransmissionUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehicleTransmission";

  private final UpdateVehicleTransmissionDTO updateVehicleTransmission;

  public VehicleTransmissionUpdater(final UpdateVehicleTransmissionDTO updateVehicleTransmission) {
    super(UpdaterType.PREVIEW, updateVehicleTransmission.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.updateVehicleTransmission = updateVehicleTransmission;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    vehicle.setTransmissions(
        this.updateVehicleTransmission.getTransmissions().stream().map(this::mapTransmission).collect(Collectors.toList()));
    return vehicle;
  }

  private TransmissionTypeDO mapTransmission(final String transmission) {
    return TransmissionTypeDO.getType(transmission);
  }

}
