package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleTransmissionUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleTransmissionUpdaterTest {

  private UpdateVehicleTransmissionDTO dto;

  private VehicleUpdater<VehicleDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto =
        UpdateVehicleTransmissionDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis())
            .transmissions(List.of("MANUAL"))
            .build();
    this.vehicleUpdater = new VehicleTransmissionUpdater(this.dto);
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
    assertThat(result.getTransmissions(), is(hasSize(1)));
    assertThat(result.getTransmissions().get(0), is(TransmissionTypeDO.MANUAL));
  }

}