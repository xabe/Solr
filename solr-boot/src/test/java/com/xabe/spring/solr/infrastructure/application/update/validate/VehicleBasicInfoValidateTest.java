package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleBasicInfoValidateTest extends ValidateBaseVehicleTest<UpdateVehicleBasicInfoDTO> {

  @Override
  protected ValidateVehicle<UpdateVehicleBasicInfoDTO> createValidate() {
    return new VehicleBasicInfoValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehicleBasicInfoDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehicleBasicInfo";
  }

  @Test
  public void notValidVehicleBrand() throws Exception {
    final UpdateVehicleBasicInfoDTO dto =
        UpdateVehicleBasicInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId")
            .timestamp(1L).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value brand"));
  }

  @Test
  public void notValidVehicleModel() throws Exception {
    final UpdateVehicleBasicInfoDTO dto =
        UpdateVehicleBasicInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).brand("brand").build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value model"));
  }

  @Test
  public void notValidVehicleFrame() throws Exception {
    final UpdateVehicleBasicInfoDTO dto =
        UpdateVehicleBasicInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).brand("brand").model("model").build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value category"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehicleBasicInfoDTO dto =
        UpdateVehicleBasicInfoDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).brand("brand")
            .storeId("storeId").timestamp(1L).model("model").category("category").build();

    this.validateVehicle.validate(dto);
  }

}