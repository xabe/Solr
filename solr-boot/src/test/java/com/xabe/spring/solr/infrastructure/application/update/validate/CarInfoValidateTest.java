package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CarInfoValidateTest extends ValidateBaseVehicleTest<UpdateCarInfoDTO> {

  @Override
  protected ValidateVehicle<UpdateCarInfoDTO> createValidate() {
    return new CarInfoValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateCarInfoDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateCarInfo";
  }

  @Test
  public void notValidVehicleDoors() throws Exception {
    final UpdateCarInfoDTO dto =
        UpdateCarInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").timestamp(1L)
            .build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value doors"));
  }

  @Test
  public void notValidVehicleType() throws Exception {
    final UpdateCarInfoDTO dto =
        UpdateCarInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").timestamp(1L)
            .doors(4).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value car type"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateCarInfoDTO dto =
        UpdateCarInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).doors(4).type("SEDAN").build();

    this.validateVehicle.validate(dto);
  }
}