package com.xabe.spring.solr.infrastructure.application.search.service.mapper;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.search.dto.VehicleDTO;

public interface VehicleMapper<T extends VehicleDO> {

  VehicleDTO mapper(T vehicle, MappingContext mappingContext);

}
