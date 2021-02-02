package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.VehiclePriceUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehiclePriceUpdaterTest {

  private UpdateVehiclePriceDTO dto;

  private VehicleUpdater<VehicleDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto =
        UpdateVehiclePriceDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis())
            .prices(PricesDTO.builder().currencyCode("EUR").prices(List.of(PriceDTO.builder().price(100L).discount(0).build())).build())
            .build();
    this.vehicleUpdater = new VehiclePriceUpdater(this.dto);
  }

  @Test
  public void shouldGetInfo() throws Exception {
    assertThat(this.vehicleUpdater.getUpdaterType(), is(UpdaterType.PREVIEW));
    assertThat(this.vehicleUpdater.getId(), is(UPDATER_ID));
    assertThat(this.vehicleUpdater.getUpdateInstant(), is(notNullValue()));
    assertThat(this.vehicleUpdater.isShouldCreateDocument(), is(false));
  }

  @Test
  public void shouldUpdateVehicleDO() throws Exception {
    //Given
    final CarDO carDO = CarDO.builder().build();
    //When

    final VehicleDO result = this.vehicleUpdater.update(carDO);
    //Then

    assertThat(result, is(notNullValue()));
    assertThat(result.getPrices(), is(notNullValue()));
    assertThat(result.getPrices().get().getCurrencyCode(), is("EUR"));
    assertThat(result.getPrices().get().getPrices(), is(hasSize(1)));
    assertThat(result.getPrices().get().getPrices().get(0).getPrice(), is(100L));
    assertThat(result.getPrices().get().getPrices().get(0).getDiscount(), is(0));
  }

}