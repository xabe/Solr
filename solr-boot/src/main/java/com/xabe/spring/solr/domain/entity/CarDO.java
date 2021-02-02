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
public class CarDO extends VehicleDO {

  private int doors;

  private CarTypeDO type;

  public CarDO(final VehicleIdDO id, final String storeId, final int vehicleVersion, final Long visibilityVersionTimestamp) {
    super(id, storeId, vehicleVersion, visibilityVersionTimestamp);
  }

  public CarDO(final CarDO car) {
    super(car);
    this.doors = car.doors;
    this.type = car.type;
  }

  public Optional<CarTypeDO> getType() {
    return Optional.ofNullable(this.type);
  }
}
