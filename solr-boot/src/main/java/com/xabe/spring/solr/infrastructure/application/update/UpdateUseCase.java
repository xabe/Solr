package com.xabe.spring.solr.infrastructure.application.update;

import com.google.common.base.Stopwatch;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public abstract class UpdateUseCase<T extends UpdateBaseDTO> {

  private final Class<T> type;

  protected final Logger logger;

  protected final ValidateVehicle<T> validator;

  protected final VehicleUpdateService vehicleUpdateService;

  private final String metricName;

  protected UpdateUseCase(final Class<T> type, final Logger logger, final ValidateVehicle<T> validator,
      final VehicleUpdateService vehicleUpdateService) {
    this.type = type;
    this.logger = logger;
    this.validator = validator;
    this.vehicleUpdateService = vehicleUpdateService;
    this.metricName = this.getClass().getSimpleName();
  }

  public Class<T> getType() {
    return this.type;
  }

  protected abstract void doUpdate(T dto);

  public void update(final T dto) {
    final Stopwatch stopwatch = Stopwatch.createStarted();

    try {
      this.doUpdate(dto);
    } finally {
      final long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
      this.logger.info("Use Case: {} executed in {} ms", this.metricName, elapsedTime);
    }
  }

  protected VehicleIdDO mapToId(final VehicleIdDTO id) {
    return VehicleIdDO.builder().id(id.getId()).type(VehicleTypeDO.getType(id.getType().name())).build();
  }

}
