package com.xabe.spring.solr.infrastructure.application.update.service.factory;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import java.time.Instant;

public interface VehicleFactory {

  VehicleDO create(VehicleIdDO id, String storeId, Instant instant);

  VehicleDO clone(VehicleDO vehicle, Instant instant);

}
