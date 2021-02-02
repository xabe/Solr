package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TruckInfoValidateTest extends ValidateBaseVehicleTest<UpdateTruckInfoDTO> {

  @Override
  protected ValidateVehicle<UpdateTruckInfoDTO> createValidate() {
    return new TruckInfoValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateTruckInfoDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateTruckInfo";
  }

  @Test
  public void notValidVehicleType() throws Exception {
    final UpdateTruckInfoDTO dto =
        UpdateTruckInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").timestamp(1L)
            .type("type").build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value truck type"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateTruckInfoDTO dto =
        UpdateTruckInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).type("CEMENT").build();

    this.validateVehicle.validate(dto);
  }

}