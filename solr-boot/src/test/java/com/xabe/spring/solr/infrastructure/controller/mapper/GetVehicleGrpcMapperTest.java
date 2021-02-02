package com.xabe.spring.solr.infrastructure.controller.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.domain.entity.ColorTypeDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.domain.entity.WheelTypeDO;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.CarDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.TruckDTO;
import com.xabe.vehicle.api.grpc.CarOuterClass.Car;
import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.ColorOuterClass.Color;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineOuterClass.Engine;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.PricesOuterClass.Prices;
import com.xabe.vehicle.api.grpc.SectionOuterClass.Section;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckOuterClass.Truck;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest.SectionDefinition;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleResponseOuterClass.GetVehicleResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetVehicleGrpcMapperTest {

  private GetVehicleGrpcMapper mapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper = new GetVehicleGrpcMapperImpl();
  }

  @Test
  public void givenAGetVehicleRequestWhenInvokeMapToDefinitionDTOThenReturnDefinitionDTO() throws Exception {
    //Given
    final GetVehicleRequest request = GetVehicleRequest.newBuilder().setFilter("filter").addAllSections(List.of(
        SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("filterQuery").build())).build();

    //When
    final DefinitionDTO result = this.mapper.mapToDefinitionDTO(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getFilter(), is("filter"));
    assertThat(result.getSectionDefinitions(), is(hasSize(1)));
    assertThat(result.getSectionDefinitions().get(0).getSequenceNo(), is(0));
    assertThat(result.getSectionDefinitions().get(0).getFilterQuery(), is("filterQuery"));
  }

  @Test
  public void givenAGetVehicleRequestWhenInvokeMapToParamsDTOThenReturnParamsDTO() throws Exception {
    //Given
    final GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setFilter("filter").setLocale("es-ES").setStoreId("storeId").setFilterHasPrices(true)
            .setVisibilityVersion(10L)
            .addAllSections(List.of(
                SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("filterQuery").build())).build();

    //When
    final ParamsDTO result = this.mapper.mapToParamsDTO(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getLocale(), is("es-ES"));
    assertThat(result.getStoreId(), is("storeId"));
    assertThat(result.getVisibilityVersion(), is(10L));
    assertThat(result.isFilterHasPrice(), is(Boolean.TRUE));
  }

  @Test
  public void givenASectionsDTOWithCarDTOWhenInvokeMapGetVehicleResponseThenReturnGetVehicleResponse() throws Exception {
    //Given
    final CarDTO car =
        CarDTO.builder().vehicleId(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).category(List.of("1", "2"))
            .brand("brand")
            .model("model")
            .colors(List.of(ColorDTO.builder().id("idColor").type(ColorTypeDO.SOLID.name()).name("rojos").build()))
            .engines(List.of(EngineDTO.builder().type("W").fuel("PETROL").build()))
            .prices(PricesDTO.builder().currencyCode("EUR")
                .prices(List.of(PriceDTO.builder().price(10L).discount(0).priceFormat("format").build())).build())
            .transmissions(List.of(TransmissionTypeDO.AUTOMATIC.name(), TransmissionTypeDO.MANUAL.name()))
            .wheels(List.of(WheelDTO.builder().count(4).size(22).type(WheelTypeDO.ALL_SEASONS.name()).build()))
            .doors(4)
            .type(CarTypeDO.COUPE.name())
            .build();
    final SectionDTO section = SectionDTO.builder().sequenceNo(0).elements(List.of(car)).build();
    final SectionsDTO sections = SectionsDTO.builder().sections(List.of(section)).build();

    //When
    final GetVehicleResponse result = this.mapper.mapGetVehicleResponse(sections);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(1));
    final Section sectionsResult = result.getSections(0);
    assertThat(sectionsResult.getSequenceNo(), is(0));
    assertThat(sectionsResult.getElementsCount(), is(1));
    final Car carResult = sectionsResult.getElementsList().get(0).getCar();
    assertThat(carResult.getVehicleId().getId(), is("id"));
    assertThat(carResult.getVehicleId().getType(), is(VehicleType.VEHICLE_CAR));
    assertThat(carResult.getCategoryCount(), is(2));
    assertThat(carResult.getCategoryList(), is(List.of("1", "2")));
    assertThat(carResult.getBrand(), is("brand"));
    assertThat(carResult.getModel(), is("model"));
    assertThat(carResult.getColorsCount(), is(1));
    final Color colorResult = carResult.getColorsList().get(0);
    assertThat(colorResult.getId(), is("idColor"));
    assertThat(colorResult.getType(), is(ColorType.COLOR_SOLID));
    assertThat(colorResult.getName(), is("rojos"));
    assertThat(carResult.getEnginesCount(), is(1));
    final Engine engineResult = carResult.getEnginesList().get(0);
    assertThat(engineResult.getType(), is(EngineType.ENGINE_W));
    assertThat(engineResult.getFuel(), is(FuelType.FUEL_PETROL));
    final Prices pricesResult = carResult.getPrices();
    assertThat(pricesResult, is(notNullValue()));
    assertThat(pricesResult.getCurrencyCode(), is("EUR"));
    assertThat(pricesResult.getPricesCount(), is(1));
    assertThat(pricesResult.getPricesList().get(0).getPrice(), is(10L));
    assertThat(pricesResult.getPricesList().get(0).getDiscount(), is(0));
    assertThat(pricesResult.getPricesList().get(0).getPriceFormat(), is("format"));
    assertThat(carResult.getTransmissionsCount(), is(2));
    assertThat(carResult.getTransmissionsList().get(0), is(TransmissionType.TRANSMISSION_AUTOMATIC));
    assertThat(carResult.getTransmissionsList().get(1), is(TransmissionType.TRANSMISSION_MANUAL));
    assertThat(carResult.getWheelsCount(), is(1));
    assertThat(carResult.getWheelsList().get(0).getCount(), is(4));
    assertThat(carResult.getWheelsList().get(0).getSize(), is(22));
    assertThat(carResult.getWheelsList().get(0).getType(), is(WheelType.WHEEL_ALL_SEASONS));
    assertThat(carResult.getDoors(), is(4));
    assertThat(carResult.getType(), is(CarType.CAR_COUPE));
  }

  @Test
  public void givenASectionsDTOWithTruckDTOWhenInvokeMapGetVehicleResponseThenReturnGetVehicleResponse() throws Exception {
    //Given
    final TruckDTO truck =
        TruckDTO.builder().vehicleId(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).category(List.of("1", "2"))
            .brand("brand")
            .model("model")
            .colors(List.of(ColorDTO.builder().id("idColor").type(ColorTypeDO.SOLID.name()).name("rojos").build()))
            .engines(List.of(EngineDTO.builder().type("W").fuel("PETROL").build()))
            .prices(PricesDTO.builder().currencyCode("EUR")
                .prices(List.of(PriceDTO.builder().price(10L).discount(0).priceFormat("format").build())).build())
            .transmissions(List.of(TransmissionTypeDO.AUTOMATIC.name(), TransmissionTypeDO.MANUAL.name()))
            .wheels(List.of(WheelDTO.builder().count(4).size(22).type(WheelTypeDO.ALL_SEASONS.name()).build()))
            .type(TruckTypeDO.CEMENT.name())
            .build();
    final SectionDTO section = SectionDTO.builder().sequenceNo(0).elements(List.of(truck)).build();
    final SectionsDTO sections = SectionsDTO.builder().sections(List.of(section)).build();

    //When
    final GetVehicleResponse result = this.mapper.mapGetVehicleResponse(sections);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(1));
    final Section sectionsResult = result.getSections(0);
    assertThat(sectionsResult.getSequenceNo(), is(0));
    assertThat(sectionsResult.getElementsCount(), is(1));
    final Truck truckResult = sectionsResult.getElementsList().get(0).getTruck();
    assertThat(truckResult.getVehicleId().getId(), is("id"));
    assertThat(truckResult.getVehicleId().getType(), is(VehicleType.VEHICLE_CAR));
    assertThat(truckResult.getCategoryCount(), is(2));
    assertThat(truckResult.getCategoryList(), is(List.of("1", "2")));
    assertThat(truckResult.getBrand(), is("brand"));
    assertThat(truckResult.getModel(), is("model"));
    assertThat(truckResult.getColorsCount(), is(1));
    final Color colorResult = truckResult.getColorsList().get(0);
    assertThat(colorResult.getId(), is("idColor"));
    assertThat(colorResult.getType(), is(ColorType.COLOR_SOLID));
    assertThat(colorResult.getName(), is("rojos"));
    assertThat(truckResult.getEnginesCount(), is(1));
    final Engine engineResult = truckResult.getEnginesList().get(0);
    assertThat(engineResult.getType(), is(EngineType.ENGINE_W));
    assertThat(engineResult.getFuel(), is(FuelType.FUEL_PETROL));
    final Prices pricesResult = truckResult.getPrices();
    assertThat(pricesResult, is(notNullValue()));
    assertThat(pricesResult.getCurrencyCode(), is("EUR"));
    assertThat(pricesResult.getPricesCount(), is(1));
    assertThat(pricesResult.getPricesList().get(0).getPrice(), is(10L));
    assertThat(pricesResult.getPricesList().get(0).getDiscount(), is(0));
    assertThat(pricesResult.getPricesList().get(0).getPriceFormat(), is("format"));
    assertThat(truckResult.getTransmissionsCount(), is(2));
    assertThat(truckResult.getTransmissionsList().get(0), is(TransmissionType.TRANSMISSION_AUTOMATIC));
    assertThat(truckResult.getTransmissionsList().get(1), is(TransmissionType.TRANSMISSION_MANUAL));
    assertThat(truckResult.getWheelsCount(), is(1));
    assertThat(truckResult.getWheelsList().get(0).getCount(), is(4));
    assertThat(truckResult.getWheelsList().get(0).getSize(), is(22));
    assertThat(truckResult.getWheelsList().get(0).getType(), is(WheelType.WHEEL_ALL_SEASONS));
    assertThat(truckResult.getType(), is(TruckType.TRUCK_CEMENT));
  }

}