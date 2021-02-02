package com.xabe.spring.solr.domain.entity;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
public abstract class VehicleDO implements DO {

  private VehicleIdDO id;

  private String storeId;

  private String category;

  private Set<String> tags = new HashSet<>();

  private String brand;

  private String model;

  private List<EngineDO> engines = Collections.emptyList();

  private PricesDO prices;

  private List<WheelDO> wheels = Collections.emptyList();

  private List<ColorDO> colors = Collections.emptyList();

  private List<TransmissionTypeDO> transmissions = Collections.emptyList();

  private Long updateVersion;

  private Long visibilityVersionTimestamp;

  private Map<String, Instant> updateInstants = new HashMap<>();

  // this is the draft version, meaning the number of times the component was cloned because it was
  // required to make changes that shouldn't be visible at the moment of updating the object
  private Integer vehicleVersion;

  public VehicleDO(final VehicleIdDO id, final String storeId, final Integer vehicleVersion, final Long visibilityVersionTimestamp) {
    this.id = id;
    this.storeId = storeId;
    this.vehicleVersion = vehicleVersion;
    this.visibilityVersionTimestamp = visibilityVersionTimestamp;
    this.updateVersion = -1L;
  }

  public VehicleDO(final VehicleDO vehicleDO) {
    this.id = vehicleDO.id;
    this.storeId = vehicleDO.storeId;
    this.vehicleVersion = vehicleDO.vehicleVersion;
    this.updateVersion = -1L;
    this.category = vehicleDO.category;
    this.engines = vehicleDO.engines;
    this.wheels = vehicleDO.wheels;
    this.transmissions = vehicleDO.transmissions;
    this.colors = vehicleDO.colors;
    this.brand = vehicleDO.brand;
    this.model = vehicleDO.model;
    this.tags = vehicleDO.tags;
    this.prices = vehicleDO.prices;
    this.visibilityVersionTimestamp = vehicleDO.visibilityVersionTimestamp;
  }

  public Instant getUpdateInstant(final String updateId) {
    return this.updateInstants.getOrDefault(updateId, Instant.MIN);
  }

  public void addUpdateInstant(final String updateId, final Instant instant) {
    this.updateInstants.put(updateId, instant);
  }

  public Optional<PricesDO> getPrices() {
    return Optional.ofNullable(this.prices);
  }

  public boolean hasPrices() {
    return this.getPrices().map(prices -> CollectionUtils.isNotEmpty(prices.getPrices())).orElse(false);
  }
}
