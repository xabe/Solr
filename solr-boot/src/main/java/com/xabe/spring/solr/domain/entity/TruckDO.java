package com.xabe.spring.solr.domain.entity;

import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
public class TruckDO extends VehicleDO {

  private TruckTypeDO type;

  public TruckDO(final VehicleIdDO id, final String storeId, final int vehicleVersion, final Long visibilityVersionTimestamp) {
    super(id, storeId, vehicleVersion, visibilityVersionTimestamp);
  }

  public TruckDO(final TruckDO truck) {
    super(truck);
    this.type = truck.type;
  }

  public Optional<TruckTypeDO> getType() {
    return Optional.ofNullable(this.type);
  }

}
