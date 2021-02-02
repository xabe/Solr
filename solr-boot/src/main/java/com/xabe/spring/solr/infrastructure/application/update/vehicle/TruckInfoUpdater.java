package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;

public class TruckInfoUpdater extends VehicleUpdater<TruckDO> {

  public static final String UPDATER_ID = "truckInfo";

  private final UpdateTruckInfoDTO dto;

  public TruckInfoUpdater(final UpdateTruckInfoDTO dto) {
    super(UpdaterType.LIVE, dto.getTimestamp(), UPDATER_ID, false);
    this.dto = dto;
  }

  @Override
  public TruckDO update(final TruckDO truck) {
    truck.setType(TruckTypeDO.getType(this.dto.getType()));
    return truck;
  }
}
