package com.xabe.spring.solr.infrastructure.application.update.service;

import com.google.common.base.Stopwatch;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.repository.update.VehicleUpdateRepository;
import com.xabe.spring.solr.infrastructure.application.update.service.factory.VehicleFactory;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleUpdateService {

  private final Logger logger;

  @Qualifier("retryTemplateSolr")
  private final RetryTemplate retryTemplate;

  private final VehicleUpdateRepository vehicleUpdateRepository;

  private final VehicleFactory vehicleFactory;

  @Value("${default-vehicle-old-versions-saved:1}")
  private final int defaultCommCompOldVersionsSaved;

  public void delete(final VehicleIdDO id, final String storeId) {
    this.retryTemplate.execute(arg -> {
      final List<VehicleDO> vehicles = this.vehicleUpdateRepository.findVehiclesByStoreId(id, storeId);
      this.vehicleUpdateRepository.delete(vehicles);
      this.logger.debug("Delete vehicle for ID: {} and Store ID: {}", id, storeId);
      return null;
    });
  }

  public void update(final VehicleIdDO id, final String storeId, final VehicleUpdater updater) {
    this.retryTemplate.execute(arg -> {
      this.logger.debug("Update vehicle {} for id={}", updater.getClass().getSimpleName(), id);
      final Stopwatch stopwatch = Stopwatch.createStarted();

      final List<VehicleDO> vehicles = this.vehicleUpdateRepository.findVehiclesByStoreId(id, storeId);

      this.logger.debug("Fetching versions for id={} items {} in {} ms", id, vehicles.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));

      if (vehicles.isEmpty() && !updater.isShouldCreateDocument()) {
        this.logger.debug("Not saving elements for id={} in {} ms", id, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return null;
      }

      final List<VehicleDO> result;
      if (vehicles.isEmpty()) {
        result = this.createVehicle(id, storeId, updater.getUpdateInstant());
      } else {
        vehicles.sort(Comparator.comparing(VehicleDO::getUpdateVersion).reversed());
        result = this.updateVehicle(vehicles, updater, storeId);
      }

      stopwatch.reset().start();
      result.forEach(this.updateGridElement(updater));
      this.logger.debug("Saving {} elements for id={} in {} ms", result.size(), id, stopwatch.elapsed(TimeUnit.MILLISECONDS));

      return null;
    });
  }

  private List<VehicleDO> createVehicle(final VehicleIdDO id, final String storeId, final Instant instant) {
    this.logger.debug("Creating new Vehicle with id {} and storeId {}", id.getId(), storeId);
    return List.of(this.vehicleFactory.create(id, storeId, instant));
  }

  private List<VehicleDO> updateVehicle(final List<VehicleDO> components, final VehicleUpdater updater, final String storeId) {
    final VehicleDO vehicleDO = components.get(0);
    this.logger.debug("Update Vehicle with id {} and storeId {}", vehicleDO.getId(), storeId);

    // live updates mean "apply the change now and make it visible"
    // therefore should be applied to any existing version of the commercial component
    // filters elements that can't be updated because the stored data is newer than the one in the
    // update function
    final List<VehicleDO> updatableVehicles = this.filterAndCloneVehicles(components, updater, storeId).stream()
        .filter(vehicle -> vehicle.getUpdateInstant(updater.getId()).isBefore(updater.getUpdateInstant())).collect(Collectors.toList());

    final List<VehicleDO> removableGridElements = this.getRemovableVehicles(components);

    this.vehicleUpdateRepository.delete(removableGridElements);
    return updatableVehicles.stream().filter(cc -> !removableGridElements.contains(cc)).collect(Collectors.toList());
  }

  private List<VehicleDO> filterAndCloneVehicles(final List<VehicleDO> vehicles, final VehicleUpdater updater, final String storeId) {
    final VehicleDO vehicle = vehicles.get(0);
    if (updater.getUpdaterType() == VehicleUpdater.UpdaterType.PREVIEW) {
      this.logger.debug("Applying live updates vehicle id {} for storeId {}", vehicle.getId(), storeId);
      this.logger.debug("Cloning vehicle {} for storeId {}", vehicle.getId(), storeId);
      return List.of(this.cloneGridElement(vehicle, updater.getUpdateInstant()));
    }
    return vehicles;
  }

  private VehicleDO cloneGridElement(final VehicleDO vehicle, final Instant instant) {
    return this.vehicleFactory.clone(vehicle, instant);
  }

  private List<VehicleDO> getRemovableVehicles(final List<VehicleDO> components) {
    return components.stream().skip(this.defaultCommCompOldVersionsSaved).collect(Collectors.toList());
  }

  private Consumer<VehicleDO> updateGridElement(final VehicleUpdater updater) {
    return vehicle -> {
      vehicle.addUpdateInstant(updater.getId(), updater.getUpdateInstant());
      this.vehicleUpdateRepository.updateVehicle(updater.update(vehicle));
    };
  }

}