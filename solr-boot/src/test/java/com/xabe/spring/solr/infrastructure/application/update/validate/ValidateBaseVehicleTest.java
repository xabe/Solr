package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class ValidateBaseVehicleTest<T extends UpdateBaseDTO> {

  protected ValidateVehicle<T> validateVehicle;

  @BeforeEach
  public void setUp() throws Exception {
    this.validateVehicle = this.createValidate();
  }

  protected abstract ValidateVehicle<T> createValidate();

  protected abstract UpdateBaseDTO.UpdateBaseDTOBuilder<T, ? extends UpdateBaseDTOBuilder> createBuilder();

  protected abstract String getId();

  @Test
  public void notValidVehicleIDNull() throws Exception {
    final T dto = this.createBuilder().build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value id"));
  }

  @Test
  public void notValidVehicleIDIdEmpty() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value id"));
  }

  @Test
  public void notValidVehicleIDTypeNull() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().id("id").build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value id"));
  }

  @Test
  public void notValidVehicleIDTypeUnspecified() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.UNSPECIFIED).build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value id"));
  }

  @Test
  public void notValidVehicleStoreId() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value store id"));
  }

  @Test
  public void notValidVehicleTimestamp() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value timestamp"));
  }

  @Test
  public void notValidVehicleTimestampZero() throws Exception {
    final T dto = this.createBuilder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build()).storeId("storeId").timestamp(0L)
        .build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value timestamp"));
  }

}