package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.TextDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleColorValidateTest extends ValidateBaseVehicleTest<UpdateVehicleColorDTO> {

  @Override
  protected ValidateVehicle<UpdateVehicleColorDTO> createValidate() {
    return new VehicleColorValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehicleColorDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehicleColor";
  }

  @Test
  public void notValidVehicleColors() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value colors"));
  }

  @Test
  public void notValidVehicleColorsId() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).colors(List.of(ColorDTO.builder().build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value id color"));
  }

  @Test
  public void notValidVehicleColorsType() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).colors(List.of(ColorDTO.builder().id("id").build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value type"));
  }

  @Test
  public void notValidVehicleColorsNames() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).colors(List.of(ColorDTO.builder().id("id").type("SOLID").build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value colors names"));
  }

  @Test
  public void notValidVehicleColorsNamesText() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L)
            .colors(List.of(ColorDTO.builder().id("id").type("SOLID").colorNames(List.of(TextDTO.builder().build())).build())).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value text"));
  }

  @Test
  public void notValidVehicleColorsNamesLocale() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L)
            .colors(List.of(ColorDTO.builder().id("id").type("SOLID").colorNames(List.of(TextDTO.builder().text("rojo").build())).build()))
            .build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value locale"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehicleColorDTO dto =
        UpdateVehicleColorDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L)
            .colors(List.of(
                ColorDTO.builder().id("id").type("SOLID").colorNames(List.of(TextDTO.builder().text("rojo").locale("es-ES").build()))
                    .build()))
            .build();

    this.validateVehicle.validate(dto);
  }

}