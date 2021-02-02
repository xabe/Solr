package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleTransmissionValidateTest extends ValidateBaseVehicleTest<UpdateVehicleTransmissionDTO> {

  @Override
  protected ValidateVehicle<UpdateVehicleTransmissionDTO> createValidate() {
    return new VehicleTransmissionValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehicleTransmissionDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehicleTransmission";
  }

  @Test
  public void notValidVehicleTransmission() throws Exception {
    final UpdateVehicleTransmissionDTO dto =
        UpdateVehicleTransmissionDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value transmission"));
  }

  @Test
  public void notValidVehicleWheelsTransmission() throws Exception {
    final UpdateVehicleTransmissionDTO dto =
        UpdateVehicleTransmissionDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).transmissions(List.of("")).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value transmission type"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehicleTransmissionDTO dto =
        UpdateVehicleTransmissionDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).transmissions(List.of("MANUAL")).build();

    this.validateVehicle.validate(dto);
  }

}