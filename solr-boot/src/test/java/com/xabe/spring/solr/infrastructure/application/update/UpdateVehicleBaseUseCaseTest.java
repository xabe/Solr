package com.xabe.spring.solr.infrastructure.application.update;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO;
import com.xabe.spring.solr.infrastructure.application.update.service.VehicleUpdateService;
import com.xabe.spring.solr.infrastructure.application.update.validate.ValidateVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

public abstract class UpdateVehicleBaseUseCaseTest<D extends UpdateBaseDTO> {

  private ValidateVehicle<D> validateVehicle;

  private VehicleUpdateService vehicleUpdateService;

  private UpdateUseCase<D> updateUseCase;

  @BeforeEach
  public void setUp() throws Exception {
    this.validateVehicle = mock(ValidateVehicle.class);
    this.vehicleUpdateService = mock(VehicleUpdateService.class);
    this.updateUseCase = this.createUseCase(mock(Logger.class), this.validateVehicle, this.vehicleUpdateService);
  }

  protected abstract UpdateUseCase<D> createUseCase(Logger logger, ValidateVehicle<D> validateVehicle,
      VehicleUpdateService vehicleUpdateService);

  @Test
  public void shouldUpdateVehicle() throws Exception {
    //Given
    final D dto = this.createDTO();
    final ArgumentCaptor<VehicleIdDO> captor = ArgumentCaptor.forClass(VehicleIdDO.class);

    //When
    this.updateUseCase.update(dto);

    //Then
    verify(this.validateVehicle).validate(eq(dto));
    verify(this.vehicleUpdateService).update(captor.capture(), eq("storeId"), any());
    final VehicleIdDO result = captor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getType(), is(VehicleTypeDO.CAR));
  }

  protected abstract D createDTO();

}
