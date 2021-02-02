package com.xabe.spring.solr.infrastructure.controller;

import com.xabe.spring.solr.infrastructure.application.update.UpdateUseCase;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.spring.solr.infrastructure.controller.mapper.UpdateVehicleGrpcMapper;
import com.xabe.vehicle.api.grpc.update.UpdateVehicleServiceGrpc.UpdateVehicleServiceImplBase;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoRequestOuterClass.UpdateCarInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoResponseOuterClass.UpdateCarInfoResponse;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoRequestOuterClass.UpdateTruckInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoResponseOuterClass.UpdateTruckInfoResponse;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoResponse.UpdateVehicleBasicResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorResponseOuterClass.UpdateVehicleColorResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineResponseOuterClass.UpdateVehicleEngineResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceResponseOuterClass.UpdateVehiclePriceResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionResponseOuterClass.UpdateVehicleTransmissionResponse;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelResponseOuterClass.UpdateVehicleWheelResponse;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;

@GrpcService
public class UpdateVehicleGrpcController extends UpdateVehicleServiceImplBase {

  private static final String SUCCESS = "Success";

  private final Logger logger;

  private final UpdateVehicleGrpcMapper grpcVehicleMapper;

  private final Map<Class, UpdateUseCase> useCaseMap;

  public UpdateVehicleGrpcController(final Logger logger, final UpdateVehicleGrpcMapper updateVehicleGrpcMapper,
      final List<UpdateUseCase> updateVehicleBasicInfoUseCase) {
    this.logger = logger;
    this.grpcVehicleMapper = updateVehicleGrpcMapper;
    this.useCaseMap = updateVehicleBasicInfoUseCase.stream().collect(Collectors.toMap(UpdateUseCase::getType, Function.identity()));
  }

  @Override
  public void updateVehicleBasicInfo(final UpdateVehicleBasicRequest request,
      final StreamObserver<UpdateVehicleBasicResponse> responseObserver) {
    this.logger.debug("Received update vehicle basic info request {}", request);

    final UpdateVehicleBasicResponse.Builder responseBuilder = UpdateVehicleBasicResponse.newBuilder();

    this.useCaseMap.get(UpdateVehicleBasicInfoDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateVehicleEngine(final UpdateVehicleEngineRequest request,
      final StreamObserver<UpdateVehicleEngineResponse> responseObserver) {
    this.logger.debug("Received update vehicle engine request {}", request);

    final UpdateVehicleEngineResponse.Builder responseBuilder = UpdateVehicleEngineResponse.newBuilder();

    this.useCaseMap.get(UpdateVehicleEngineDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateVehiclePrice(final UpdateVehiclePriceRequest request,
      final StreamObserver<UpdateVehiclePriceResponse> responseObserver) {
    this.logger.debug("Received update vehicle price request {}", request);

    final UpdateVehiclePriceResponse.Builder responseBuilder = UpdateVehiclePriceResponse.newBuilder();

    this.useCaseMap.get(UpdateVehiclePriceDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateVehicleWheel(final UpdateVehicleWheelRequest request,
      final StreamObserver<UpdateVehicleWheelResponse> responseObserver) {
    this.logger.debug("Received update vehicle wheels request {}", request);

    final UpdateVehicleWheelResponse.Builder responseBuilder = UpdateVehicleWheelResponse.newBuilder();

    this.useCaseMap.get(UpdateVehicleWheelDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateVehicleColor(final UpdateVehicleColorRequest request,
      final StreamObserver<UpdateVehicleColorResponse> responseObserver) {
    this.logger.debug("Received update vehicle color request {}", request);

    final UpdateVehicleColorResponse.Builder responseBuilder = UpdateVehicleColorResponse.newBuilder();

    this.useCaseMap.get(UpdateVehicleColorDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateVehicleTransmission(final UpdateVehicleTransmissionRequest request,
      final StreamObserver<UpdateVehicleTransmissionResponse> responseObserver) {
    this.logger.debug("Received update vehicle transmission request {}", request);

    final UpdateVehicleTransmissionResponse.Builder responseBuilder = UpdateVehicleTransmissionResponse.newBuilder();

    this.useCaseMap.get(UpdateVehicleTransmissionDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateCarInfo(final UpdateCarInfoRequest request, final StreamObserver<UpdateCarInfoResponse> responseObserver) {
    this.logger.debug("Received update car info request {}", request);

    final UpdateCarInfoResponse.Builder responseBuilder = UpdateCarInfoResponse.newBuilder();

    this.useCaseMap.get(UpdateCarInfoDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }

  @Override
  public void updateTruckInfo(final UpdateTruckInfoRequest request, final StreamObserver<UpdateTruckInfoResponse> responseObserver) {
    this.logger.debug("Received update truck info request {}", request);

    final UpdateTruckInfoResponse.Builder responseBuilder = UpdateTruckInfoResponse.newBuilder();

    this.useCaseMap.get(UpdateTruckInfoDTO.class).update(this.grpcVehicleMapper.requestToDTO(request));

    responseObserver.onNext(responseBuilder.setId(request.getId()).setStatus(SUCCESS).build());

    responseObserver.onCompleted();
  }
}
