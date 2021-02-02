package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VehicleBasicInfoUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehicleBasicInfo";

  public static final String REGEX = ",";

  private final UpdateVehicleBasicInfoDTO updateVehicleBasicInfo;

  public VehicleBasicInfoUpdater(final UpdateVehicleBasicInfoDTO updateVehicleBasicInfo) {
    super(UpdaterType.LIVE, updateVehicleBasicInfo.getTimestamp(), UPDATER_ID, Boolean.TRUE);
    this.updateVehicleBasicInfo = updateVehicleBasicInfo;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    vehicle.setStoreId(this.updateVehicleBasicInfo.getStoreId());
    vehicle.setBrand(this.updateVehicleBasicInfo.getBrand());
    vehicle.setModel(this.updateVehicleBasicInfo.getModel());
    vehicle.setCategory(this.updateVehicleBasicInfo.getCategory());
    vehicle.setTags(Stream.of(this.updateVehicleBasicInfo.getCategory().split(REGEX)).map(String::trim).collect(Collectors.toSet()));
    return vehicle;
  }
}
