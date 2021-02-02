package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleEngineValidateTest extends ValidateBaseVehicleTest<UpdateVehicleEngineDTO> {

  @Override
  protected ValidateVehicle<UpdateVehicleEngineDTO> createValidate() {
    return new VehicleEngineValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehicleEngineDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehicleEngine";
  }

  @Test
  public void notValidVehicleEngines() throws Exception {
    final UpdateVehicleEngineDTO dto =
        UpdateVehicleEngineDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value engines"));
  }

  @Test
  public void notValidVehicleEngineType() throws Exception {
    final UpdateVehicleEngineDTO dto =
        UpdateVehicleEngineDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).engines(List.of(EngineDTO.builder().build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value type"));
  }

  @Test
  public void notValidVehicleEngineFuel() throws Exception {
    final UpdateVehicleEngineDTO dto =
        UpdateVehicleEngineDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).engines(List.of(EngineDTO.builder().type("type").build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value fuel"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehicleEngineDTO dto =
        UpdateVehicleEngineDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).engines(List.of(EngineDTO.builder().type("type").fuel("fuel").build())).build();

    this.validateVehicle.validate(dto);
  }

}