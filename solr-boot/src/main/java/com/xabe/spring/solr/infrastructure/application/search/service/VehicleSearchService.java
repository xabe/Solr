package com.xabe.spring.solr.infrastructure.application.search.service;

import static com.xabe.spring.solr.infrastructure.application.util.ParallelStreams.allOfOrException;

import com.google.common.base.Stopwatch;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.exception.VehicleSectionConcurrentException;
import com.xabe.spring.solr.domain.repository.search.VehicleSearchRepository;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.service.mapper.MappingContext;
import com.xabe.spring.solr.infrastructure.application.search.service.mapper.VehiclesMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleSearchService {

  private final Logger logger;

  private final VehicleSearchRepository vehicleSearchRepository;

  private final VehiclesMapper vehiclesMapper;

  private final ExecutorService executorService;

  public SectionsDTO getVehicles(final DefinitionDTO definition, final ParamsDTO params) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final SectionsDTO sections = this.findSections(definition, params);
    this.logger.debug("{}ms elapsed finding Vehicles Elements with params {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), params);
    return sections;
  }

  private SectionsDTO findSections(final DefinitionDTO definition, final ParamsDTO params) {
    try {
      final MappingContext mappingContext = MappingContext.builder().locale(params.getLocale()).build();
      final List<SectionDefinitionDTO> sectionDefinitions = definition.getSectionDefinitions();
      final List<CompletableFuture<SectionDTO>> completableFutures = IntStream.range(0, sectionDefinitions.size()).mapToObj(
          index -> CompletableFuture.supplyAsync(
              () -> this.findSection(definition.getFilter(), sectionDefinitions.get(index), params, sectionDefinitions.subList(0, index),
                  mappingContext),
              this.executorService))
          .collect(Collectors.toList());
      final List<SectionDTO> sections = allOfOrException(completableFutures, this.executorService).get();
      return SectionsDTO.builder().sections(sections).build();
    } catch (final Exception e) {
      this.logger.error("Error get sections pointers: {}", e.getMessage(), e);
      throw new VehicleSectionConcurrentException(e);
    }
  }

  private SectionDTO findSection(final String filter, final SectionDefinitionDTO sectionDefinition,
      final ParamsDTO params,
      final List<SectionDefinitionDTO> excludedSections, final MappingContext mappingContext) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final List<VehicleDO> vehicles =
        this.vehicleSearchRepository.findVehicleBySectionDefinition(filter, sectionDefinition, excludedSections, params);
    final SectionDTO result =
        SectionDTO.builder().sequenceNo(sectionDefinition.getSequenceNo())
            .elements(vehicles.stream().map(item -> this.vehiclesMapper.mapper(item, mappingContext)).collect(Collectors.toList())).build();
    this.logger.info("{}ms elapsed finding section sequence {} with params {} ",
        stopwatch.elapsed(TimeUnit.MILLISECONDS),
        sectionDefinition.getSequenceNo(), params);
    return result;
  }

}
