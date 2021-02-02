package com.xabe.spring.solr.infrastructure.application.search.service.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.domain.entity.ColorDO;
import com.xabe.spring.solr.domain.entity.ColorTypeDO;
import com.xabe.spring.solr.domain.entity.EngineDO;
import com.xabe.spring.solr.domain.entity.EngineTypeDO;
import com.xabe.spring.solr.domain.entity.FuelTypeDO;
import com.xabe.spring.solr.domain.entity.PriceDO;
import com.xabe.spring.solr.domain.entity.PricesDO;
import com.xabe.spring.solr.domain.entity.TextDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.entity.WheelDO;
import com.xabe.spring.solr.domain.entity.WheelSizeDO;
import com.xabe.spring.solr.domain.entity.WheelTypeDO;
import com.xabe.spring.solr.infrastructure.application.search.dto.CarDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.TruckDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.VehicleDTO;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehiclesMapperTest {

  private VehiclesMapper mapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper = new VehiclesMapper();
  }

  @Test
  public void givenACarDOWhenInvokeMapperThenReturnCarDTO() throws Exception {
    //Given
    final CarDO car = new CarDO(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build(), "storeId", 1, 1L);
    car.setTags(Set.of("1", "2"));
    car.setBrand("brand");
    car.setModel("model");
    car.setColors(List.of(
        ColorDO.builder().id("idColor").type(ColorTypeDO.SOLID)
            .names(List.of(TextDO.builder().locale("es-ES").text("rojo").build(), TextDO.builder().locale("en-GB").text("red").build()))
            .build()));
    car.setEngines(List.of(EngineDO.builder().type(EngineTypeDO.IN_LINE).fuel(FuelTypeDO.PETROL).build()));
    car.setPrices(PricesDO.builder().currencyCode("EUR")
        .prices(List.of(PriceDO.builder().price(10L).discount(0).build())).build());
    car.setTransmissions(List.of(TransmissionTypeDO.AUTOMATIC, TransmissionTypeDO.MANUAL));
    car.setWheels(List.of(WheelDO.builder().count(4).size(WheelSizeDO.R_16).type(WheelTypeDO.ALL_SEASONS).build()));
    car.setDoors(4);
    car.setType(CarTypeDO.COUPE);

    //When
    final VehicleDTO result = this.mapper.mapper(car, MappingContext.builder().locale("es-ES").build());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getVehicleId().getId(), is("id"));
    assertThat(result.getVehicleId().getType().name(), is("CAR"));
    assertThat(result.getCategory(), is(containsInAnyOrder("1", "2")));
    assertThat(result.getBrand(), is("brand"));
    assertThat(result.getModel(), is("model"));
    assertThat(result.getColors(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getId(), is("idColor"));
    assertThat(result.getColors().get(0).getType(), is("SOLID"));
    assertThat(result.getColors().get(0).getName(), is("rojo"));
    assertThat(result.getEngines(), is(hasSize(1)));
    assertThat(result.getEngines().get(0).getType(), is("IN_LINE"));
    assertThat(result.getEngines().get(0).getFuel(), is("PETROL"));
    assertThat(result.getPrices(), is(notNullValue()));
    assertThat(result.getPrices().getCurrencyCode(), is("EUR"));
    assertThat(result.getPrices().getPrices(), is(hasSize(1)));
    assertThat(result.getPrices().getPrices().get(0).getPrice(), is(10L));
    assertThat(result.getPrices().getPrices().get(0).getDiscount(), is(0));
    assertThat(result.getPrices().getPrices().get(0).getPriceFormat(), is("€ 10.00"));
    assertThat(result.getTransmissions(), is(containsInAnyOrder("AUTOMATIC", "MANUAL")));
    assertThat(result.getWheels(), is(hasSize(1)));
    assertThat(result.getWheels().get(0).getCount(), is(4));
    assertThat(result.getWheels().get(0).getSize(), is(16));
    assertThat(result.getWheels().get(0).getType(), is("ALL_SEASONS"));
    final CarDTO carResult = CarDTO.class.cast(result);
    assertThat(carResult, is(notNullValue()));
    assertThat(carResult.getDoors(), is(4));
    assertThat(carResult.getType(), is("COUPE"));
  }

  @Test
  public void givenATruckDOWhenInvokeMapperThenReturnCarDTO() throws Exception {
    //Given
    final TruckDO truck = new TruckDO(VehicleIdDO.builder().id("id").type(VehicleTypeDO.TRUCK).build(), "storeId", 1, 1L);
    truck.setTags(Set.of("1", "2"));
    truck.setBrand("brand");
    truck.setModel("model");
    truck.setColors(List.of(
        ColorDO.builder().id("idColor").type(ColorTypeDO.SOLID)
            .names(List.of(TextDO.builder().locale("es-ES").text("rojo").build(), TextDO.builder().locale("en-GB").text("red").build()))
            .build()));
    truck.setEngines(List.of(EngineDO.builder().type(EngineTypeDO.IN_LINE).fuel(FuelTypeDO.PETROL).build()));
    truck.setPrices(PricesDO.builder().currencyCode("EUR")
        .prices(List.of(PriceDO.builder().price(10L).discount(0).build())).build());
    truck.setTransmissions(List.of(TransmissionTypeDO.AUTOMATIC, TransmissionTypeDO.MANUAL));
    truck.setWheels(List.of(WheelDO.builder().count(4).size(WheelSizeDO.R_16).type(WheelTypeDO.ALL_SEASONS).build()));
    truck.setType(TruckTypeDO.TRAILER);

    //When
    final VehicleDTO result = this.mapper.mapper(truck, MappingContext.builder().locale("es-ES").build());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getVehicleId().getId(), is("id"));
    assertThat(result.getVehicleId().getType().name(), is("TRUCK"));
    assertThat(result.getCategory(), is(containsInAnyOrder("1", "2")));
    assertThat(result.getBrand(), is("brand"));
    assertThat(result.getModel(), is("model"));
    assertThat(result.getColors(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getId(), is("idColor"));
    assertThat(result.getColors().get(0).getType(), is("SOLID"));
    assertThat(result.getColors().get(0).getName(), is("rojo"));
    assertThat(result.getEngines(), is(hasSize(1)));
    assertThat(result.getEngines().get(0).getType(), is("IN_LINE"));
    assertThat(result.getEngines().get(0).getFuel(), is("PETROL"));
    assertThat(result.getPrices(), is(notNullValue()));
    assertThat(result.getPrices().getCurrencyCode(), is("EUR"));
    assertThat(result.getPrices().getPrices(), is(hasSize(1)));
    assertThat(result.getPrices().getPrices().get(0).getPrice(), is(10L));
    assertThat(result.getPrices().getPrices().get(0).getDiscount(), is(0));
    assertThat(result.getPrices().getPrices().get(0).getPriceFormat(), is("€ 10.00"));
    assertThat(result.getTransmissions(), is(containsInAnyOrder("AUTOMATIC", "MANUAL")));
    assertThat(result.getWheels(), is(hasSize(1)));
    assertThat(result.getWheels().get(0).getCount(), is(4));
    assertThat(result.getWheels().get(0).getSize(), is(16));
    assertThat(result.getWheels().get(0).getType(), is("ALL_SEASONS"));
    final TruckDTO truckResult = TruckDTO.class.cast(result);
    assertThat(truckResult, is(notNullValue()));
    assertThat(truckResult.getType(), is("TRAILER"));
  }

}