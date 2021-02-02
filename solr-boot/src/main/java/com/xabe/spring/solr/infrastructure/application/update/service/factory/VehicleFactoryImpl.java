package com.xabe.spring.solr.infrastructure.application.update.service.factory;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.springframework.stereotype.Component;

@Component
public class VehicleFactoryImpl implements VehicleFactory {

  private final Map<VehicleTypeDO, Function<FactoryContext, VehicleDO>> createFactoryMap;

  private final Map<VehicleTypeDO, Function<FactoryContext, VehicleDO>> cloneFactoryMap;

  public VehicleFactoryImpl() {
    this.createFactoryMap = new HashMap<>();
    this.cloneFactoryMap = new HashMap<>();
    this.createFactoryMap.put(VehicleTypeDO.CAR, this::createCar);
    this.cloneFactoryMap.put(VehicleTypeDO.CAR, this::cloneCar);
    this.createFactoryMap.put(VehicleTypeDO.TRUCK, this::createTruck);
    this.cloneFactoryMap.put(VehicleTypeDO.TRUCK, this::cloneTruck);
  }

  private VehicleDO cloneCar(final FactoryContext factoryContext) {
    final VehicleDO vehicle = factoryContext.getVehicle();
    final CarDO clone = new CarDO(CarDO.class.cast(vehicle));
    clone.setVehicleVersion(vehicle.getVehicleVersion() + 1);
    clone.setUpdateInstants(vehicle.getUpdateInstants());
    clone.setVisibilityVersionTimestamp(factoryContext.getInstant().toEpochMilli());
    return clone;
  }

  private VehicleDO cloneTruck(final FactoryContext factoryContext) {
    final VehicleDO vehicle = factoryContext.getVehicle();
    final TruckDO clone = new TruckDO(TruckDO.class.cast(vehicle));
    clone.setVehicleVersion(vehicle.getVehicleVersion() + 1);
    clone.setUpdateInstants(vehicle.getUpdateInstants());
    clone.setVisibilityVersionTimestamp(factoryContext.getInstant().toEpochMilli());
    return clone;
  }

  private VehicleDO createCar(final FactoryContext factoryContext) {
    return new CarDO(factoryContext.getId(), factoryContext.storeId, 1, factoryContext.getInstant().toEpochMilli());
  }

  private VehicleDO createTruck(final FactoryContext factoryContext) {
    return new TruckDO(factoryContext.getId(), factoryContext.storeId, 1, factoryContext.getInstant().toEpochMilli());
  }

  @Override
  public VehicleDO create(final VehicleIdDO id, final String storeId, final Instant instant) {
    return this.getFactory(this.createFactoryMap, id.getType())
        .apply(FactoryContext.builder().id(id).storeId(storeId).instant(instant).build());
  }

  @Override
  public VehicleDO clone(final VehicleDO vehicle, final Instant instant) {
    return this.getFactory(this.cloneFactoryMap, vehicle.getId().getType())
        .apply(FactoryContext.builder().vehicle(vehicle).instant(instant).build());
  }

  private Function<FactoryContext, VehicleDO> getFactory(final Map<VehicleTypeDO, Function<FactoryContext, VehicleDO>> map,
      final VehicleTypeDO type) {
    final Function<FactoryContext, VehicleDO> factory = map.get(type);
    if (Objects.isNull(factory)) {
      throw new UnsupportedOperationException();
    }
    return factory;
  }

  @Value
  @Builder(toBuilder = true)
  @EqualsAndHashCode
  @ToString
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class FactoryContext {

    private final VehicleIdDO id;

    private final String storeId;

    private final VehicleDO vehicle;

    private final Instant instant;
  }
}
