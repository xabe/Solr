package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleWheelUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleWheelUpdaterTest {

  private UpdateVehicleWheelDTO dto;

  private VehicleUpdater<VehicleDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto =
        UpdateVehicleWheelDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis())
            .wheels(List.of(WheelDTO.builder().size(16).type("SUMMER").count(4).build()))
            .build();
    this.vehicleUpdater = new VehicleWheelUpdater(this.dto);
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
    assertThat(result.getWheels(), is(hasSize(1)));
    assertThat(result.getWheels().get(0).getType().name(), is("SUMMER"));
    assertThat(result.getWheels().get(0).getSize().name(), is("R_16"));
    assertThat(result.getWheels().get(0).getCount(), is(4));
  }

}