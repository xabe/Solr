package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;

public class CarInfoUpdater extends VehicleUpdater<CarDO> {

  public static final String UPDATER_ID = "carInfo";

  private final UpdateCarInfoDTO dto;

  public CarInfoUpdater(final UpdateCarInfoDTO dto) {
    super(UpdaterType.LIVE, dto.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.dto = dto;
  }

  @Override
  public CarDO update(final CarDO car) {
    car.setDoors(this.dto.getDoors());
    car.setType(CarTypeDO.getType(this.dto.getType()));
    return car;
  }
}
