package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.WheelDO;
import com.xabe.spring.solr.domain.entity.WheelSizeDO;
import com.xabe.spring.solr.domain.entity.WheelTypeDO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import java.util.stream.Collectors;

public class VehicleWheelUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehicleWheel";

  private final UpdateVehicleWheelDTO updateVehicleWheel;

  public VehicleWheelUpdater(final UpdateVehicleWheelDTO updateVehicleWheel) {
    super(UpdaterType.PREVIEW, updateVehicleWheel.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.updateVehicleWheel = updateVehicleWheel;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    vehicle.setWheels(this.updateVehicleWheel.getWheels().stream().map(this::mapWheel).collect(Collectors.toList()));
    return vehicle;
  }

  private WheelDO mapWheel(final WheelDTO wheel) {
    return WheelDO.builder().count(wheel.getCount()).type(WheelTypeDO.getType(wheel.getType())).size(WheelSizeDO.getSize(wheel.getSize()))
        .build();
  }

}
