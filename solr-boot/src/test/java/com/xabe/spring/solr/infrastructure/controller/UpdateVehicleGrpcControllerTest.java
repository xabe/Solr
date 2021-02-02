package com.xabe.spring.solr.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.infrastructure.application.update.UpdateUseCase;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.spring.solr.infrastructure.controller.mapper.UpdateVehicleGrpcMapper;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoRequestOuterClass.UpdateCarInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoRequestOuterClass.UpdateTruckInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.slf4j.Logger;

class UpdateVehicleGrpcControllerTest {

  private UpdateVehicleGrpcMapper updateVehicleGrpcMapper;

  private UpdateUseCase<UpdateVehicleBasicInfoDTO> updateVehicleBasicInfoUseCase;

  private UpdateUseCase<UpdateVehicleEngineDTO> updateVehicleEngineUseCase;

  private UpdateUseCase<UpdateVehiclePriceDTO> updateVehiclePriceUseCase;

  private UpdateUseCase<UpdateVehicleWheelDTO> updateVehicleWheelUseCase;

  private UpdateUseCase<UpdateVehicleColorDTO> updateVehicleColorUseCase;

  private UpdateUseCase<UpdateVehicleTransmissionDTO> updateVehicleTransmissionUseCase;

  private UpdateUseCase<UpdateCarInfoDTO> updateCarInfoUseCase;

  private UpdateUseCase<UpdateTruckInfoDTO> updateTruckInfoUseCase;

  private UpdateVehicleGrpcController updateVehicleGrpcController;

  @BeforeEach
  public void setUp() throws Exception {
    this.updateVehicleGrpcMapper = mock(UpdateVehicleGrpcMapper.class);
    this.updateVehicleBasicInfoUseCase = this.initUseCase(UpdateVehicleBasicInfoDTO.class);
    this.updateVehicleEngineUseCase = this.initUseCase(UpdateVehicleEngineDTO.class);
    this.updateVehiclePriceUseCase = this.initUseCase(UpdateVehiclePriceDTO.class);
    this.updateVehicleWheelUseCase = this.initUseCase(UpdateVehicleWheelDTO.class);
    this.updateVehicleColorUseCase = this.initUseCase(UpdateVehicleColorDTO.class);
    this.updateVehicleTransmissionUseCase = this.initUseCase(UpdateVehicleTransmissionDTO.class);
    this.updateCarInfoUseCase = this.initUseCase(UpdateCarInfoDTO.class);
    this.updateTruckInfoUseCase = this.initUseCase(UpdateTruckInfoDTO.class);
    this.updateVehicleGrpcController =
        new UpdateVehicleGrpcController(mock(Logger.class), this.updateVehicleGrpcMapper,
            List.of(this.updateVehicleBasicInfoUseCase, this.updateVehicleEngineUseCase, this.updateVehiclePriceUseCase,
                this.updateVehicleWheelUseCase, this.updateVehicleColorUseCase, this.updateVehicleTransmissionUseCase,
                this.updateCarInfoUseCase, this.updateTruckInfoUseCase));
  }

  private <T extends UpdateBaseDTO> UpdateUseCase<T> initUseCase(final Class<T> clazz) {
    final UpdateUseCase<T> updateUseCase = mock(UpdateUseCase.class);
    when(updateUseCase.getType()).thenReturn(clazz);
    return updateUseCase;
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehicleBasicInfoThenReturnResponse() throws Exception {
    final UpdateVehicleBasicRequest request = UpdateVehicleBasicRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehicleBasicInfoDTO updateVehicleBasicInfoDTO = UpdateVehicleBasicInfoDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehicleBasicInfoDTO);

    this.updateVehicleGrpcController.updateVehicleBasicInfo(request, streamObserver);

    verify(this.updateVehicleBasicInfoUseCase).update(eq(updateVehicleBasicInfoDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehicleEngineThenReturnResponse() throws Exception {
    final UpdateVehicleEngineRequest request = UpdateVehicleEngineRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehicleEngineDTO updateVehicleEngineDTO = UpdateVehicleEngineDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehicleEngineDTO);

    this.updateVehicleGrpcController.updateVehicleEngine(request, streamObserver);

    verify(this.updateVehicleEngineUseCase).update(eq(updateVehicleEngineDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehiclePriceThenReturnResponse() throws Exception {
    final UpdateVehiclePriceRequest request = UpdateVehiclePriceRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehiclePriceDTO updateVehiclePriceDTO = UpdateVehiclePriceDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehiclePriceDTO);

    this.updateVehicleGrpcController.updateVehiclePrice(request, streamObserver);

    verify(this.updateVehiclePriceUseCase).update(eq(updateVehiclePriceDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehicleWheelThenReturnResponse() throws Exception {
    final UpdateVehicleWheelRequest request = UpdateVehicleWheelRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehicleWheelDTO updateVehicleWheelDTO = UpdateVehicleWheelDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehicleWheelDTO);

    this.updateVehicleGrpcController.updateVehicleWheel(request, streamObserver);

    verify(this.updateVehicleWheelUseCase).update(eq(updateVehicleWheelDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehicleColorThenReturnResponse() throws Exception {
    final UpdateVehicleColorRequest request = UpdateVehicleColorRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehicleColorDTO updateVehicleColorDTO = UpdateVehicleColorDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehicleColorDTO);

    this.updateVehicleGrpcController.updateVehicleColor(request, streamObserver);

    verify(this.updateVehicleColorUseCase).update(eq(updateVehicleColorDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateVehicleTransmissionThenReturnResponse() throws Exception {
    final UpdateVehicleTransmissionRequest request = UpdateVehicleTransmissionRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateVehicleTransmissionDTO updateVehicleTransmissionDTO = UpdateVehicleTransmissionDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateVehicleTransmissionDTO);

    this.updateVehicleGrpcController.updateVehicleTransmission(request, streamObserver);

    verify(this.updateVehicleTransmissionUseCase).update(eq(updateVehicleTransmissionDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateCarInfoThenReturnResponse() throws Exception {
    final UpdateCarInfoRequest request = UpdateCarInfoRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateCarInfoDTO updateCarInfoDTO = UpdateCarInfoDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateCarInfoDTO);

    this.updateVehicleGrpcController.updateCarInfo(request, streamObserver);

    verify(this.updateCarInfoUseCase).update(eq(updateCarInfoDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenARequestWhenInvokeUpdateTruckInfoThenReturnResponse() throws Exception {
    final UpdateTruckInfoRequest request = UpdateTruckInfoRequest.getDefaultInstance();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final UpdateTruckInfoDTO updateTruckInfoDTO = UpdateTruckInfoDTO.builder().build();

    when(this.updateVehicleGrpcMapper.requestToDTO(eq(request))).thenReturn(updateTruckInfoDTO);

    this.updateVehicleGrpcController.updateTruckInfo(request, streamObserver);

    verify(this.updateTruckInfoUseCase).update(eq(updateTruckInfoDTO));
    final InOrder inOrder = inOrder(streamObserver);
    inOrder.verify(streamObserver).onNext(any());
    inOrder.verify(streamObserver).onCompleted();
  }
}