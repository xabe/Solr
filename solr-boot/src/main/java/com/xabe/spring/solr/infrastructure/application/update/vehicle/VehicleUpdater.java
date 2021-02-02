package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.util.TimeUtil;
import java.time.Instant;
import lombok.Getter;

@Getter
public abstract class VehicleUpdater<T extends VehicleDO> {

  private final UpdaterType updaterType;

  private final Instant updateInstant;

  private final String id;

  private final boolean shouldCreateDocument;

  protected VehicleUpdater(final UpdaterType updaterType, final Long updateTimestamp, final String id,
      final boolean shouldCreateDocument) {
    this.updaterType = updaterType;
    this.id = id;
    this.updateInstant = Instant.ofEpochMilli(TimeUtil.getMillis(updateTimestamp));
    this.shouldCreateDocument = shouldCreateDocument;
  }

  public abstract T update(T vehicle);

  public enum UpdaterType {
    PREVIEW,
    LIVE
  }
}
