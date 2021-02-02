package com.xabe.spring.solr.acceptance.util;

import static com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType.COLOR_METALLIC;
import static com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType.ENGINE_IN_LINE;
import static com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType.ENGINE_W;
import static com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType.FUEL_PETROL;
import static com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType.TRANSMISSION_AUTOMATIC;
import static com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType.TRANSMISSION_MANUAL;
import static com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType.VEHICLE_CAR;
import static com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType.VEHICLE_TRUCK;
import static com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType.WHEEL_ALL_SEASONS;

import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.EngineOuterClass.Engine;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleIdOuterClass.VehicleId;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelOuterClass.Wheel;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoRequestOuterClass.UpdateCarInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoRequestOuterClass.UpdateTruckInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.Color;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.I18NText;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Price;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Prices;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.apache.solr.client.solrj.SolrClient;

public class LoadData {

  public static final String MAZDA = "mazda3";

  public static final String BMW = "bmw3";

  public static final String TRUCK_MERCEDES = "truckWithoutPrices";

  private final SolrClient solrClient;

  public LoadData(final SolrClient solrClient) {
    this.solrClient = solrClient;
  }

  public void init(final String storeId) {
    this.delete();
    this.commit();
    final List<I18NText> colors = List.of(
        I18NText.newBuilder().setText("rojo").setLocale("es-ES").build(),
        I18NText.newBuilder().setText("red").setLocale("en-GB").build(),
        I18NText.newBuilder().setText("azul").setLocale("es-ES").build(),
        I18NText.newBuilder().setText("blue").setLocale("en-GB").build());
    final List<TransmissionType> transmissions = List.of(TRANSMISSION_MANUAL, TRANSMISSION_AUTOMATIC);

    final VehicleInfoDTO mazda = VehicleInfoDTO.builder().id(MAZDA).storeId(storeId).vehicleType(VEHICLE_CAR)
        .timestamp(System.currentTimeMillis())
        .brand("Mazda").model("Mazda3").category("HATCHBACK,SEDAN")
        .engines(List.of(Engine.newBuilder().setType(ENGINE_IN_LINE).setFuel(FUEL_PETROL).build()))
        .prices(Prices.newBuilder().setCurrencyCode("EUR").addAllPrices(List.of(Price.newBuilder().setPrice(25000).setDiscount(5).build()))
            .build())
        .wheels(List.of(Wheel.newBuilder().setCount(4).setType(WHEEL_ALL_SEASONS).setSize(18).build()))
        .colors(List.of(Color.newBuilder().setId("1").setType(COLOR_METALLIC).addAllColorNames(colors).build())).
            transmissions(transmissions)
        .build();
    this.createVehicle(mazda);
    this.createCar(mazda, 5, CarType.CAR_HATCHBACK);

    final VehicleInfoDTO bwm = VehicleInfoDTO.builder().id(BMW).storeId(storeId).vehicleType(VEHICLE_CAR)
        .timestamp(System.currentTimeMillis())
        .brand("BMW").model("3").category("HATCHBACK,SEDAN")
        .engines(List.of(Engine.newBuilder().setType(ENGINE_IN_LINE).setFuel(FUEL_PETROL).build()))
        .prices(Prices.newBuilder().setCurrencyCode("EUR").addAllPrices(List.of(Price.newBuilder().setPrice(35000).setDiscount(5).build()))
            .build())
        .wheels(List.of(Wheel.newBuilder().setCount(4).setType(WHEEL_ALL_SEASONS).setSize(19).build()))
        .colors(List.of(Color.newBuilder().setId("1").setType(COLOR_METALLIC).addAllColorNames(colors).build())).
            transmissions(transmissions)
        .build();
    this.createVehicle(bwm);
    this.createCar(bwm, 4, CarType.CAR_HATCHBACK);

    final VehicleInfoDTO truckWithoutPrices =
        VehicleInfoDTO.builder().id(TRUCK_MERCEDES).storeId(storeId).vehicleType(VEHICLE_TRUCK)
            .timestamp(System.currentTimeMillis())
            .brand("Mercedes").model("Actros").category("TRAILER")
            .engines(List.of(Engine.newBuilder().setType(ENGINE_W).setFuel(FUEL_PETROL).build()))
            .wheels(List.of(Wheel.newBuilder().setCount(16).setType(WHEEL_ALL_SEASONS).setSize(22).build()))
            .colors(List.of(Color.newBuilder().setId("1").setType(COLOR_METALLIC).addAllColorNames(colors).build())).
            transmissions(List.of(TRANSMISSION_AUTOMATIC))
            .build();
    this.createVehicle(truckWithoutPrices);
    this.createTruck(truckWithoutPrices, TruckType.TRUCK_TRAILER);
  }

  private void createVehicle(final VehicleInfoDTO vehicleInfo) {
    final VehicleId vehicleId = VehicleId.newBuilder().setType(vehicleInfo.getVehicleType()).setId(vehicleInfo.getId()).build();
    final UpdateVehicleBasicRequest updateVehicleBasicRequest =
        UpdateVehicleBasicRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp())
            .setBrand(vehicleInfo.getBrand()).setCategory(vehicleInfo.getCategory()).setModel(vehicleInfo.getModel()).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleBasicInfo(updateVehicleBasicRequest);
    this.commit();
    final UpdateVehicleEngineRequest updateVehicleEngineRequest =
        UpdateVehicleEngineRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp())
            .addAllEngines(vehicleInfo.getEngines()).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleEngine(updateVehicleEngineRequest);
    this.commit();
    vehicleInfo.getPrices().ifPresent(this.createPrices(vehicleInfo, vehicleId));
    final UpdateVehicleWheelRequest updateVehicleWheelRequest =
        UpdateVehicleWheelRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp())
            .addAllWheels(vehicleInfo.getWheels()).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleWheel(updateVehicleWheelRequest);
    this.commit();
    final UpdateVehicleColorRequest updateVehicleColorRequest =
        UpdateVehicleColorRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp()).addAllColors(vehicleInfo.getColors()).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleColor(updateVehicleColorRequest);
    this.commit();
    final UpdateVehicleTransmissionRequest updateVehicleTransmissionRequest =
        UpdateVehicleTransmissionRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp())
            .addAllTransmissions(vehicleInfo.getTransmissions()).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleTransmission(updateVehicleTransmissionRequest);
    this.commit();
  }

  private Consumer<Prices> createPrices(final VehicleInfoDTO vehicleInfo, final VehicleId vehicleId) {
    return prices -> {
      final UpdateVehiclePriceRequest updateVehiclePriceRequest =
          UpdateVehiclePriceRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
              .setTimestamp(vehicleInfo.getTimestamp()).setPrices(prices).build();
      GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehiclePrice(updateVehiclePriceRequest);
      this.commit();
    };
  }

  private void createCar(final VehicleInfoDTO vehicleInfo, final int doors, final CarType carType) {
    final VehicleId vehicleId = VehicleId.newBuilder().setType(vehicleInfo.getVehicleType()).setId(vehicleInfo.getId()).build();
    final UpdateCarInfoRequest updateCarInfoRequest =
        UpdateCarInfoRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId())
            .setTimestamp(vehicleInfo.getTimestamp()).setDoors(doors).setType(carType).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateCarInfo(updateCarInfoRequest);
    this.commit();
  }

  private void createTruck(final VehicleInfoDTO vehicleInfo, final TruckType truckType) {
    final VehicleId vehicleId = VehicleId.newBuilder().setType(vehicleInfo.getVehicleType()).setId(vehicleInfo.getId()).build();
    final UpdateTruckInfoRequest updateTruckInfoRequest =
        UpdateTruckInfoRequest.newBuilder().setId(vehicleId).setStoreId(vehicleInfo.getStoreId()).setTimestamp(vehicleInfo.getTimestamp())
            .setType(truckType).build();
    GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateTruckInfo(updateTruckInfoRequest);
    this.commit();
  }

  public void commit() {
    try {
      this.solrClient.commit();
    } catch (final Exception var2) {
    }
  }

  public void delete() {
    try {
      this.solrClient.deleteByQuery("*:*");
    } catch (final Exception var2) {
    }
  }

  @Value
  @Builder(toBuilder = true)
  @EqualsAndHashCode
  @ToString
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static class VehicleInfoDTO {

    private final String id;

    private final String storeId;

    private final VehicleType vehicleType;

    private final Long timestamp;

    private final String brand;

    private final String model;

    private final String category;

    private final List<Engine> engines;

    private final Prices prices;

    private final List<Wheel> wheels;

    private final List<Color> colors;

    private final List<TransmissionType> transmissions;

    public Optional<Prices> getPrices() {
      return Optional.ofNullable(this.prices);
    }
  }
}