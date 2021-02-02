package com.xabe.spring.solr.infrastructure.application.search;

import com.google.common.base.Stopwatch;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.service.VehicleSearchService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVehicleUseCase {

  private final Logger logger;

  private final VehicleSearchService vehicleSearchService;

  public SectionsDTO getVehicles(final DefinitionDTO definition, final ParamsDTO params) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final SectionsDTO result = this.vehicleSearchService.getVehicles(definition, params);
    this.logger.info("Get vehicles in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return result;
  }
}