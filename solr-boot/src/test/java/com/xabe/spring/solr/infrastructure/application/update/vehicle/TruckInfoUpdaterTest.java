package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.TruckInfoUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TruckInfoUpdaterTest {

  private UpdateTruckInfoDTO dto;

  private VehicleUpdater<TruckDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto = UpdateTruckInfoDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis()).type("CEMENT").build();
    this.vehicleUpdater = new TruckInfoUpdater(this.dto);
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
    final TruckDO truckDO = TruckDO.builder().build();
    //When

    final TruckDO result = this.vehicleUpdater.update(truckDO);
    //Then

    assertThat(result, is(notNullValue()));
    assertThat(result.getType().get().name(), is(this.dto.getType()));
  }

}