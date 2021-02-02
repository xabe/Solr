package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleBasicInfoUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductBasicInfoUpdaterTest {

  private UpdateVehicleBasicInfoDTO dto;

  private VehicleUpdater<VehicleDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto =
        UpdateVehicleBasicInfoDTO.builder().storeId("storeId").model("model").timestamp(System.currentTimeMillis()).brand("brand")
            .category("category,tags")
            .build();
    this.vehicleUpdater = new VehicleBasicInfoUpdater(this.dto);
  }

  @Test
  public void shouldGetInfo() throws Exception {
    assertThat(this.vehicleUpdater.getUpdaterType(), is(UpdaterType.LIVE));
    assertThat(this.vehicleUpdater.getId(), is(UPDATER_ID));
    assertThat(this.vehicleUpdater.getUpdateInstant(), is(notNullValue()));
    assertThat(this.vehicleUpdater.isShouldCreateDocument(), is(true));
  }

  @Test
  public void shouldUpdateVehicleDO() throws Exception {
    //Given
    final CarDO carDO = CarDO.builder().build();
    //When

    final VehicleDO result = this.vehicleUpdater.update(carDO);
    //Then

    assertThat(result, is(notNullValue()));
    assertThat(result.getBrand(), is(this.dto.getBrand()));
    assertThat(result.getModel(), is(this.dto.getModel()));
    assertThat(result.getCategory(), is(this.dto.getCategory()));
    assertThat(result.getStoreId(), is(this.dto.getStoreId()));
    assertThat(result.getTags(), is(new HashSet<>(Arrays.asList(this.dto.getCategory().split(",")))));
  }
}