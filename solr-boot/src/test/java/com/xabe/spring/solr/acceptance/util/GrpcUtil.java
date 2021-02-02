package com.xabe.spring.solr.acceptance.util;

import com.xabe.vehicle.api.grpc.search.SearchVehicleServiceGrpc;
import com.xabe.vehicle.api.grpc.search.SearchVehicleServiceGrpc.SearchVehicleServiceBlockingStub;
import com.xabe.vehicle.api.grpc.update.UpdateVehicleServiceGrpc;
import com.xabe.vehicle.api.grpc.update.UpdateVehicleServiceGrpc.UpdateVehicleServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcUtil {

  private static final GrpcUtil INSTANCE = new GrpcUtil();

  private final UpdateVehicleServiceBlockingStub updateVehicleServiceBlockingStub;

  private final SearchVehicleServiceBlockingStub searchVehicleServiceBlockingStub;

  private GrpcUtil() {
    try {
      final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("0.0.0.0", 6565).usePlaintext().build();
      this.updateVehicleServiceBlockingStub = UpdateVehicleServiceGrpc.newBlockingStub(managedChannel);
      this.searchVehicleServiceBlockingStub = SearchVehicleServiceGrpc.newBlockingStub(managedChannel);
    } catch (final Exception var2) {
      throw new RuntimeException(var2);
    }
  }

  public static GrpcUtil instance() {
    return INSTANCE;
  }

  public UpdateVehicleServiceBlockingStub getUpdateVehicleServiceBlockingStub() {
    return this.updateVehicleServiceBlockingStub;
  }

  public SearchVehicleServiceBlockingStub getSearchVehicleServiceBlockingStub() {
    return this.searchVehicleServiceBlockingStub;
  }
}
