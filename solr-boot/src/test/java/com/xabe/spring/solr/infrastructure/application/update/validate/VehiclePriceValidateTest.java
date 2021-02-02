package com.xabe.spring.solr.infrastructure.application.update.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO.UpdateBaseDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehiclePriceValidateTest extends ValidateBaseVehicleTest<UpdateVehiclePriceDTO> {

  @Override
  protected ValidateVehicle<UpdateVehiclePriceDTO> createValidate() {
    return new VehiclePriceValidate();
  }

  @Override
  protected UpdateBaseDTOBuilder createBuilder() {
    return UpdateVehiclePriceDTO.builder();
  }

  @Override
  protected String getId() {
    return "UpdateVehiclePrice";
  }

  @Test
  public void notValidVehicleCodeCurrency() throws Exception {
    final UpdateVehiclePriceDTO dto =
        UpdateVehiclePriceDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).prices(PricesDTO.builder().build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value currency code"));
  }

  @Test
  public void notValidVehiclePricesItemEmpty() throws Exception {
    final UpdateVehiclePriceDTO dto =
        UpdateVehiclePriceDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L).prices(PricesDTO.builder().currencyCode("EUR").build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value prices"));
  }

  @Test
  public void notValidVehiclePricesItem() throws Exception {
    final UpdateVehiclePriceDTO dto =
        UpdateVehiclePriceDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L)
            .prices(PricesDTO.builder().currencyCode("EUR").prices(List.of(PriceDTO.builder().build())).build()).build();

    final InvalidUpdateVehicleException result =
        Assertions.assertThrows(InvalidUpdateVehicleException.class, () -> this.validateVehicle.validate(dto));

    assertThat(result, is(notNullValue()));
    assertThat(result.getMessage(), is(this.getId() + " contains an invalid value price"));
  }

  @Test
  public void validVehicle() throws Exception {
    final UpdateVehiclePriceDTO dto =
        UpdateVehiclePriceDTO.builder().id(VehicleIdDTO.builder().id("id").type(VehicleTypeDTO.CAR).build())
            .storeId("storeId").timestamp(1L)
            .prices(PricesDTO.builder().currencyCode("EUR").prices(List.of(PriceDTO.builder().price(1L).build())).build()).build();

    this.validateVehicle.validate(dto);
  }

}