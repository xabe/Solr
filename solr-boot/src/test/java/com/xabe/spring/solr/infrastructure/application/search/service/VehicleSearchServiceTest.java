package com.xabe.spring.solr.infrastructure.application.search.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.repository.search.VehicleSearchRepository;
import com.xabe.spring.solr.infrastructure.application.search.dto.CarDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.service.mapper.MappingContext;
import com.xabe.spring.solr.infrastructure.application.search.service.mapper.VehiclesMapper;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class VehicleSearchServiceTest {

  private VehicleSearchRepository vehicleSearchRepository;

  private VehiclesMapper vehiclesMapper;

  private VehicleSearchService vehicleSearchService;

  @BeforeEach
  public void setUp() throws Exception {
    this.vehicleSearchRepository = mock(VehicleSearchRepository.class);
    this.vehiclesMapper = mock(VehiclesMapper.class);
    this.vehicleSearchService = new VehicleSearchService(mock(Logger.class), this.vehicleSearchRepository, this.vehiclesMapper,
        Executors.newFixedThreadPool(1));
  }

  @Test
  public void givenADefinitionAndParamsWithoutSectionWhenInvokeGetVehiclesThenReturnSections() throws Exception {
    //Given
    final DefinitionDTO definition = DefinitionDTO.builder().filter("filter").sectionDefinitions(List.of()).build();
    final ParamsDTO params = ParamsDTO.builder().locale("es-ES").build();

    //When
    final SectionsDTO result = this.vehicleSearchService.getVehicles(definition, params);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSections(), is(hasSize(0)));
  }

  @Test
  public void givenADefinitionAndParamsOneSectionWhenInvokeGetVehiclesThenReturnSections() throws Exception {
    //Given
    final CarDO car = new CarDO(VehicleIdDO.builder().build(), "storeId", 1, 1L);
    final SectionDefinitionDTO sectionDefinition = SectionDefinitionDTO.builder().filterQuery("filterQuery").sequenceNo(0).build();
    final DefinitionDTO definition = DefinitionDTO.builder().filter("filter").sectionDefinitions(List.of(sectionDefinition)).build();
    final ParamsDTO params = ParamsDTO.builder().locale("es-ES").build();

    when(this.vehicleSearchRepository.findVehicleBySectionDefinition(eq("filter"), eq(sectionDefinition), eq(List.of()), eq(params)))
        .thenReturn(List.of(car));
    when(this.vehiclesMapper.mapper(eq(car), eq(MappingContext.builder().locale("es-ES").build()))).thenReturn(CarDTO.builder().build());

    //When
    final SectionsDTO result = this.vehicleSearchService.getVehicles(definition, params);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSections(), is(hasSize(1)));
    assertThat(result.getSections().get(0).getElements(), is(hasSize(1)));
    assertThat(result.getSections().get(0).getSequenceNo(), is(0));
  }

  @Test
  public void givenADefinitionAndParamsTwoSectionWhenInvokeGetVehiclesThenReturnSections() throws Exception {
    //Given
    final CarDO car = new CarDO(VehicleIdDO.builder().build(), "storeId", 1, 1L);
    final SectionDefinitionDTO sectionDefinition = SectionDefinitionDTO.builder().filterQuery("filterQuery").sequenceNo(0).build();
    final SectionDefinitionDTO sectionDefinition2 = SectionDefinitionDTO.builder().filterQuery("filterQuery2").sequenceNo(1).build();
    final DefinitionDTO definition =
        DefinitionDTO.builder().filter("filter").sectionDefinitions(List.of(sectionDefinition, sectionDefinition2)).build();
    final ParamsDTO params = ParamsDTO.builder().locale("es-ES").build();

    when(this.vehicleSearchRepository.findVehicleBySectionDefinition(eq("filter"), eq(sectionDefinition), eq(List.of()), eq(params)))
        .thenReturn(List.of(car));
    when(this.vehicleSearchRepository
        .findVehicleBySectionDefinition(eq("filter"), eq(sectionDefinition2), eq(List.of(sectionDefinition)), eq(params)))
        .thenReturn(List.of(car, car));
    when(this.vehiclesMapper.mapper(eq(car), eq(MappingContext.builder().locale("es-ES").build()))).thenReturn(CarDTO.builder().build());

    //When
    final SectionsDTO result = this.vehicleSearchService.getVehicles(definition, params);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSections(), is(hasSize(2)));
    assertThat(result.getSections().get(0).getElements(), is(hasSize(1)));
    assertThat(result.getSections().get(0).getSequenceNo(), is(0));
    assertThat(result.getSections().get(1).getElements(), is(hasSize(2)));
    assertThat(result.getSections().get(1).getSequenceNo(), is(1));
  }

}