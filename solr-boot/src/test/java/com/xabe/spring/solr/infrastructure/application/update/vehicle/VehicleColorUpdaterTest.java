package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import static com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleColorUpdater.UPDATER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.TextDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleColorUpdaterTest {

  private UpdateVehicleColorDTO dto;

  private VehicleUpdater<VehicleDO> vehicleUpdater;

  @BeforeEach
  public void setUp() throws Exception {
    this.dto =
        UpdateVehicleColorDTO.builder().storeId("storeId").timestamp(System.currentTimeMillis())
            .colors(List.of(
                ColorDTO.builder().id("id").type("SOLID").colorNames(List.of(TextDTO.builder().text("rojo").locale("es-ES").build()))
                    .build()))
            .build();
    this.vehicleUpdater = new VehicleColorUpdater(this.dto);
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
    assertThat(result.getColors(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getId(), is("id"));
    assertThat(result.getColors().get(0).getType().name(), is("SOLID"));
    assertThat(result.getColors().get(0).getNames(), is(hasSize(1)));
    assertThat(result.getColors().get(0).getNames().get(0).getLocale(), is("es-ES"));
    assertThat(result.getColors().get(0).getNames().get(0).getText(), is("rojo"));
  }

}