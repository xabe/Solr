package com.xabe.spring.solr.infrastructure.controller;

import com.google.common.base.Stopwatch;
import com.xabe.spring.solr.infrastructure.application.search.GetVehicleUseCase;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.controller.mapper.GetVehicleGrpcMapper;
import com.xabe.vehicle.api.grpc.search.SearchVehicleServiceGrpc.SearchVehicleServiceImplBase;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleResponseOuterClass.GetVehicleResponse;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;

@GrpcService
public class SearchVehicleGrpcController extends SearchVehicleServiceImplBase {

  private final Logger logger;

  private final GetVehicleGrpcMapper getVehicleGrpcMapper;

  private final GetVehicleUseCase getVehicleUseCase;

  public SearchVehicleGrpcController(final Logger logger,
      final GetVehicleGrpcMapper getVehicleGrpcMapper, final GetVehicleUseCase getVehicleUseCase) {
    this.logger = logger;
    this.getVehicleGrpcMapper = getVehicleGrpcMapper;
    this.getVehicleUseCase = getVehicleUseCase;
  }

  @Override
  public void getVehicle(final GetVehicleRequest request, final StreamObserver<GetVehicleResponse> responseObserver) {
    this.logger.info("Received grpc Request to retrieve Vehicles");
    final Stopwatch stopwatch = Stopwatch.createStarted();

    final DefinitionDTO definition = this.getVehicleGrpcMapper.mapToDefinitionDTO(request);
    final ParamsDTO params = this.getVehicleGrpcMapper.mapToParamsDTO(request);
    final SectionsDTO sections = this.getVehicleUseCase.getVehicles(definition, params);

    final GetVehicleResponse response = this.getVehicleGrpcMapper.mapGetVehicleResponse(sections);
    this.logger.info("getVehicle returned with params: {}, response in ms: {}, number of sections: {}", params,
        stopwatch.elapsed(TimeUnit.MILLISECONDS), response.getSectionsCount());

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
