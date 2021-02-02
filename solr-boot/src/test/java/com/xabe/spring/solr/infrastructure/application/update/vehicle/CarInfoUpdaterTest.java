package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.CarInfoUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarInfoUpdaterTest {

  private UpdateCarInfoDTO dto;

  private VehicleUpdater<CarDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto = UpdateCarInfoDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis()).doors(5).type("SEDAN").build();
    this.vehicleUpdater = new CarInfoUpdater(this.dto);
  }

  @Test
  public void shouldGetInfo() throws Exception {
    assertThat(this.vehicleUpdater.getUpdaterType(), is(UpdaterType.LIVE));
    assertThat(this.vehicleUpdater.getId(), is(UPDATER_ID));
    assertThat(this.vehicleUpdater.getUpdateInstant(), is(notNullValue()));
    assertThat(this.vehicleUpdater.isShouldCreateDocument(), is(false));
  }

  @Test
  public void shouldUpdateVehicleDO() throws Exception {
    //Given
    final CarDO carDO = CarDO.builder().build();
    //When

    final CarDO result = this.vehicleUpdater.update(carDO);
    //Then

    assertThat(result, is(notNullValue()));
    assertThat(result.getDoors(), is(this.dto.getDoors()));
    assertThat(result.getType().get().name(), is(this.dto.getType()));
  }

}