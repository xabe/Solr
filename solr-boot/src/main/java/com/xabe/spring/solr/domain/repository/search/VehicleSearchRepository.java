package com.xabe.spring.solr.domain.repository.search;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDefinitionDTO;
import java.util.List;

public interface VehicleSearchRepository {

  List<VehicleDO> findVehicleBySectionDefinition(String filter, SectionDefinitionDTO sectionDefinition,
      List<SectionDefinitionDTO> excludedSections, ParamsDTO params);
}
