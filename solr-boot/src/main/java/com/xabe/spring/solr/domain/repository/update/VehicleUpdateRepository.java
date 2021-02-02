package com.xabe.spring.solr.domain.repository.update;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import java.util.List;

public interface VehicleUpdateRepository {

  List<VehicleDO> findVehiclesByStoreId(VehicleIdDO id, String storeId);

  void delete(List<VehicleDO> vehicles);

  void updateVehicle(VehicleDO vehicle);
}
