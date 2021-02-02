package com.xabe.spring.solr.infrastructure.controller.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineOuterClass.Engine;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleIdOuterClass.VehicleId;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelOuterClass.Wheel;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateVehicleGrpcMapperTest {

  private UpdateVehicleGrpcMapper updateVehicleGrpcMapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.updateVehicleGrpcMapper = new UpdateVehicleGrpcMapperImpl();
  }

  @Test
  public void givenAUpdateVehicleBasicWhenInvokeRequestToDTOThenReturnUpdateVehicleBasicInfo() throws Exception {
    final UpdateVehicleBasicRequest request = UpdateVehicleBasicRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
        VehicleType.VEHICLE_CAR
    ).build()).setBrand("brand").setCategory("SUV").setModel("model").setStoreId("storeId").setTimestamp(1L).build();

    final UpdateVehicleBasicInfoDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getBrand(), is("brand"));
    assertThat(result.getCategory(), is("SUV"));
    assertThat(result.getModel(), is("model"));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
  }

  @Test
  public void givenAUpdateVehicleEngineWhenInvokeRequestToDTOThenReturnUpdateVehicleEngine() throws Exception {
    final UpdateVehicleEngineRequest request = UpdateVehicleEngineRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
        VehicleType.VEHICLE_CAR
    ).build()).setStoreId("storeId")
        .addAllEngines(List.of(Engine.newBuilder().setType(EngineType.ENGINE_FLAT).setFuel(FuelType.FUEL_HYDROGEN).build()))
        .setTimestamp(1L).build();

    final UpdateVehicleEngineDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getEngines(), is(hasSize(1)));
    assertThat(result.getEngines().get(0).getType(), is("FLAT"));
    assertThat(result.getEngines().get(0).getFuel(), is("HYDROGEN"));
  }

  @Test
  public void givenAUpdateVehiclePriceWhenInvokeRequestToDTOThenReturnUpdateVehiclePrice() throws Exception {
    final UpdateVehiclePriceRequest request = UpdateVehiclePriceRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
        VehicleType.VEHICLE_CAR
    ).build()).setStoreId("storeId").setPrices(
        Prices.newBuilder().setCurrencyCode("EUR").addAllPrices(List.of(Price.newBuilder().setPrice(100L).setDiscount(10).build()))
            .build())
        .setTimestamp(1L).build();

    final UpdateVehiclePriceDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getPrices().getCurrencyCode(), is("EUR"));
    assertThat(result.getPrices().getPrices(), is(hasSize(1)));
    assertThat(result.getPrices().getPrices().get(0).getPrice(), is(100L));
    assertThat(result.getPrices().getPrices().get(0).getDiscount(), is(10));
  }

  @Test
  public void givenAUpdateVehicleWheelWhenInvokeRequestToDTOThenReturnUpdateVehicleWheel() throws Exception {
    final UpdateVehicleWheelRequest request = UpdateVehicleWheelRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
        VehicleType.VEHICLE_CAR
    ).build()).setStoreId("storeId")
        .addAllWheels(List.of(Wheel.newBuilder().setCount(4).setType(WheelType.WHEEL_ALL_SEASONS).setSize(18).build()))
        .setTimestamp(1L).build();

    final UpdateVehicleWheelDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getWheels(), is(hasSize(1)));
    assertThat(result.getWheels().get(0).getCount(), is(4));
    assertThat(result.getWheels().get(0).getType(), is("ALL_SEASONS"));
    assertThat(result.getWheels().get(0).getSize(), is(18));
  }

  @Test
  public void givenAUpdateVehicleColorWhenInvokeRequestToDTOThenReturnUpdateVehicleColor() throws Exception {
    final UpdateVehicleColorRequest request = UpdateVehicleColorRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
        VehicleType.VEHICLE_CAR
    ).build()).setStoreId("storeId")
        .addAllColors(List.of(
            Color.newBuilder().setId("id").setType(ColorType.COLOR_SOLID)
                .addAllColorNames(List.of(I18NText.newBuilder().setText("rojo").setLocale("es-ES").build())).build()))
        .setTimestamp(1L).build();

    final UpdateVehicleColorDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getColors(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getId(), is("id"));
    assertThat(result.getColors().get(0).getType(), is("SOLID"));
    assertThat(result.getColors().get(0).getColorNames(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getColorNames().get(0).getLocale(), is("es-ES"));
    assertThat(result.getColors().get(0).getColorNames().get(0).getText(), is("rojo"));
  }

  @Test
  public void givenAUpdateVehicleTransmissionWhenInvokeRequestToDTOThenReturnUpdateVehicleTransmission() throws Exception {
    final UpdateVehicleTransmissionRequest request =
        UpdateVehicleTransmissionRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
            VehicleType.VEHICLE_CAR
        ).build()).setStoreId("storeId")
            .addAllTransmissions(List.of(TransmissionType.TRANSMISSION_MANUAL)).setTimestamp(1L).build();

    final UpdateVehicleTransmissionDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getTransmissions(), is(hasSize(1)));
    assertThat(result.getTransmissions().get(0), is("MANUAL"));
  }

  @Test
  public void givenAUpdateCarInfoWhenInvokeRequestToDTOThenReturnUpdateCarInfo() throws Exception {
    final UpdateCarInfoRequest request =
        UpdateCarInfoRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
            VehicleType.VEHICLE_CAR
        ).build()).setStoreId("storeId").setType(CarType.CAR_HATCHBACK)
            .setDoors(5).setTimestamp(1L).build();

    final UpdateCarInfoDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getDoors(), is(5));
    assertThat(result.getType(), is("HATCHBACK"));
  }

  @Test
  public void givenAUpdateTruckInfoWhenInvokeRequestToDTOThenReturnUpdateTruckInfo() throws Exception {
    final UpdateTruckInfoRequest request =
        UpdateTruckInfoRequest.newBuilder().setId(VehicleId.newBuilder().setId("id").setType(
            VehicleType.VEHICLE_CAR
        ).build()).setStoreId("storeId").setType(TruckType.TRUCK_TOWN).setTimestamp(1L).build();

    final UpdateTruckInfoDTO result = this.updateVehicleGrpcMapper.requestToDTO(request);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDTO.CAR));
    assertThat(result.getTimestamp(), is(1L));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getType(), is("TOWN"));
  }

}