package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.EngineDO;
import com.xabe.spring.solr.domain.entity.EngineTypeDO;
import com.xabe.spring.solr.domain.entity.FuelTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import java.util.stream.Collectors;

public class VehicleEngineUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehicleEngine";

  private final UpdateVehicleEngineDTO updateVehicleEngine;

  public VehicleEngineUpdater(final UpdateVehicleEngineDTO updateVehicleEngine) {
    super(UpdaterType.PREVIEW, updateVehicleEngine.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.updateVehicleEngine = updateVehicleEngine;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    vehicle.setEngines(this.updateVehicleEngine.getEngines().stream().map(this::mapEngine).collect(Collectors.toList()));
    return vehicle;
  }

  private EngineDO mapEngine(final EngineDTO engine) {
    final EngineDO.EngineDOBuilder builder = EngineDO.builder();
    builder.type(EngineTypeDO.value(engine.getType()));
    builder.fuel(FuelTypeDO.value(engine.getFuel()));
    return builder.build();
  }
}
