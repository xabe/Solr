package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleWheelValidateTest extends ValidateBaseVehicleTest<UpdateVehicleWheelDTO> {

  @Override
  protected ValidateVehicle<UpdateVehicleWheelDTO> createValidate() {
    return new VehicleWheelValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehicleWheelDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehicleWheel";
  }

  @Test
  public void notValidVehicleWheels() throws Exception {
    final UpdateVehicleWheelDTO dto =
        UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value wheels"));
  }

  @Test
  public void notValidVehicleWheelsCount() throws Exception {
    final UpdateVehicleWheelDTO dto =
        UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).wheels(List.of(WheelDTO.builder().build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value count"));
  }

  @Test
  public void notValidVehicleWheelsSize() throws Exception {
    final UpdateVehicleWheelDTO dto =
        UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).wheels(List.of(WheelDTO.builder().count(4).build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value size"));
  }

  @Test
  public void notValidVehicleWheelsType() throws Exception {
    final UpdateVehicleWheelDTO dto =
        UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).wheels(List.of(WheelDTO.builder().count(4).size(16).type("type").build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value type"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehicleWheelDTO dto =
        UpdateVehicleWheelDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).wheels(List.of(WheelDTO.builder().count(4).size(16).type("SUMMER").build())).build();

    this.validateVehicle.validate(dto);
  }

}