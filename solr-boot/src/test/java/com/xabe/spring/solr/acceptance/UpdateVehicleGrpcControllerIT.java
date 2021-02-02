package com.xabe.spring.solr.acceptance;

import static com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType.VEHICLE_TRUCK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.acceptance.util.GrpcUtil;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineOuterClass.Engine;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleIdOuterClass.VehicleId;
import com.xabe.vehicle.api.grpc.WheelOuterClass.Wheel;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoRequestOuterClass.UpdateTruckInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoResponseOuterClass.UpdateTruckInfoResponse;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoResponse.UpdateVehicleBasicResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.Color;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.I18NText;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorResponseOuterClass.UpdateVehicleColorResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineResponseOuterClass.UpdateVehicleEngineResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Price;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Prices;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceResponseOuterClass.UpdateVehiclePriceResponse;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionResponseOuterClass.UpdateVehicleTransmissionResponse;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelResponseOuterClass.UpdateVehicleWheelResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

public class UpdateVehicleGrpcControllerIT extends AbstractVehicleGrpc {

  public static final String SUCCESS = "Success";

  private static final String id = "truck";

  @Test
  public void shouldCreateBasicInfoTruck() throws Exception {
    //Given
    final UpdateVehicleBasicRequest updateVehicleBasicRequest = UpdateVehicleBasicRequest.newBuilder()
        .setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
        .setStoreId("sp")
        .setTimestamp(System.currentTimeMillis())
        .setBrand("brand")
        .setCategory("category")
        .setModel("model").build();

    //When
    final UpdateVehicleBasicResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleBasicInfo(updateVehicleBasicRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreateEngineTruck() throws Exception {
    //Given
    final UpdateVehicleEngineRequest updateVehicleEngineRequest =
        UpdateVehicleEngineRequest.newBuilder().setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .addAllEngines(List.of(Engine.newBuilder().setFuel(FuelType.FUEL_HYDROGEN).setType(EngineType.ENGINE_FLAT).build())).build();

    //When
    final UpdateVehicleEngineResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleEngine(updateVehicleEngineRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreatePriceTruck() throws Exception {
    //Given
    final UpdateVehiclePriceRequest updateVehiclePriceRequest =
        UpdateVehiclePriceRequest.newBuilder().setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .setPrices(
                Prices.newBuilder().setCurrencyCode("EUR").addAllPrices(List.of(Price.newBuilder().setPrice(100L).setDiscount(0).build()))
                    .build()).build();

    //When
    final UpdateVehiclePriceResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehiclePrice(updateVehiclePriceRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreateWheelTruck() throws Exception {
    //Given
    final UpdateVehicleWheelRequest updateVehicleWheelRequest =
        UpdateVehicleWheelRequest.newBuilder().setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .addAllWheels(List.of(Wheel.newBuilder().setSize(22).setType(WheelType.WHEEL_SUMMER).setCount(18).build())).build();

    //When
    final UpdateVehicleWheelResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleWheel(updateVehicleWheelRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreateColorTruck() throws Exception {
    //Given
    final UpdateVehicleColorRequest updateVehicleColorRequest =
        UpdateVehicleColorRequest.newBuilder().setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .addAllColors(List.of(
                Color.newBuilder().setId("1").setType(ColorType.COLOR_SOLID)
                    .addAllColorNames(List.of(I18NText.newBuilder().setLocale("es-ES").setText("red").build())).build()))
            .build();

    //When
    final UpdateVehicleColorResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleColor(updateVehicleColorRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreateTransmissionTruck() throws Exception {
    //Given
    final UpdateVehicleTransmissionRequest updateVehicleTransmissionRequest =
        UpdateVehicleTransmissionRequest.newBuilder()
            .setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .addAllTransmissions(List.of(TransmissionType.TRANSMISSION_AUTOMATIC, TransmissionType.TRANSMISSION_MANUAL)).build();

    //When
    final UpdateVehicleTransmissionResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleTransmission(updateVehicleTransmissionRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }

  @Test
  public void shouldCreateTruck() throws Exception {
    //Given
    final UpdateTruckInfoRequest updateTruckInfoRequest =
        UpdateTruckInfoRequest.newBuilder()
            .setId(VehicleId.newBuilder().setType(VEHICLE_TRUCK).setId(id).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .setType(TruckType.TRUCK_CEMENT).build();

    //When
    final UpdateTruckInfoResponse result =
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateTruckInfo(updateTruckInfoRequest);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getStatus(), is(SUCCESS));
  }
}

