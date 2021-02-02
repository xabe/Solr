package com.xabe.spring.solr.infrastructure.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.infrastructure.application.search.GetVehicleUseCase;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.controller.mapper.GetVehicleGrpcMapper;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleResponseOuterClass.GetVehicleResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.slf4j.Logger;

class SearchVehicleGrpcControllerTest {

  private GetVehicleGrpcMapper mapper;

  private GetVehicleUseCase useCase;

  private SearchVehicleGrpcController controller;

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper = mock(GetVehicleGrpcMapper.class);
    this.useCase = mock(GetVehicleUseCase.class);
    this.controller = new SearchVehicleGrpcController(mock(Logger.class), this.mapper, this.useCase);
  }

  @Test
  public void givenARequestWhenInvokeGetVehicleThenReturnResponse() throws Exception {
    //Given
    final GetVehicleRequest request = GetVehicleRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);

    final ParamsDTO params = ParamsDTO.builder().build();
    final DefinitionDTO definition = DefinitionDTO.builder().build();
    final SectionsDTO sections = SectionsDTO.builder().build();
    final GetVehicleResponse response = GetVehicleResponse.getDefaultInstance();

    when(this.mapper.mapToDefinitionDTO(eq(request))).thenReturn(definition);
    when(this.mapper.mapToParamsDTO(eq(request))).thenReturn(params);
    when(this.useCase.getVehicles(eq(definition), eq(params))).thenReturn(sections);
    when(this.mapper.mapGetVehicleResponse(eq(sections))).thenReturn(response);

    //When
    this.controller.getVehicle(request, streamObserver);

    //Then
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(eq(response));
    inOrder.verify(streamObserver).onCompleted();
  }

}