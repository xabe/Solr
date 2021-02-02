package com.xabe.spring.solr.infrastructure.application.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.service.VehicleSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class GetVehicleUseCaseTest {

  private VehicleSearchService vehicleSearchService;

  private GetVehicleUseCase getVehicleUseCase;

  @BeforeEach
  public void setUp() throws Exception {
    this.vehicleSearchService = mock(VehicleSearchService.class);
    this.getVehicleUseCase = new GetVehicleUseCase(mock(Logger.class), this.vehicleSearchService);
  }

  @Test
  public void givenADefinitionAndParamsWhenInvokeGetVehiclesThenReturnSectionsDTO() throws Exception {
    //Given
    final DefinitionDTO definition = DefinitionDTO.builder().build();
    final ParamsDTO param = ParamsDTO.builder().build();

    when(this.vehicleSearchService.getVehicles(eq(definition), eq(param))).thenReturn(SectionsDTO.builder().build());
    //When
    final SectionsDTO result = this.getVehicleUseCase.getVehicles(definition, param);

    //Then
    assertThat(result, is(notNullValue()));
  }

}