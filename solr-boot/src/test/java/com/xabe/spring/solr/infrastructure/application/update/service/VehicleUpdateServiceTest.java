package com.xabe.spring.solr.infrastructure.application.update.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.exception.VehicleConcurrentUpdateException;
import com.xabe.spring.solr.domain.repository.update.VehicleUpdateRepository;
import com.xabe.spring.solr.infrastructure.application.update.service.factory.VehicleFactory;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater;
import com.xabe.spring.solr.infrastructure.application.update.vehicle.VehicleUpdater.UpdaterType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

class VehicleUpdateServiceTest {

  private Logger logger;

  private VehicleUpdateRepository vehicleUpdateRepository;

  private VehicleFactory vehicleFactory;

  private VehicleUpdateService vehicleUpdateService;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.vehicleUpdateRepository = mock(VehicleUpdateRepository.class);
    this.vehicleFactory = mock(VehicleFactory.class);
    final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3,
        Collections.singletonMap(VehicleConcurrentUpdateException.class, Boolean.TRUE));
    final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(0);
    final RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(retryPolicy);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    this.vehicleUpdateService = new VehicleUpdateService(this.logger, retryTemplate, this.vehicleUpdateRepository, this.vehicleFactory, 1);
  }

  @Test
  public void shouldDeleteVehicleByIdAndStoreId() throws Exception {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().build();
    final String storeId = "sp";

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(List.of());

    //When
    this.vehicleUpdateService.delete(id, storeId);

    //Then
    verify(this.vehicleUpdateRepository).delete(eq(List.of()));
  }

  @Test
  public void shouldDeleteVehicleByIdAndStoreIdRetry() throws Exception {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().build();
    final String storeId = "sp";

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenThrow(VehicleConcurrentUpdateException.class)
        .thenThrow(VehicleConcurrentUpdateException.class).thenReturn(List.of());

    //When
    this.vehicleUpdateService.delete(id, storeId);

    //Then
    verify(this.vehicleUpdateRepository).delete(eq(List.of()));
  }

  @Test
  public void notShouldCreateVehicleWhenUpdaterIsNotValid() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.LIVE, System.currentTimeMillis(), "id", false) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        return null;
      }
    };

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(List.of());

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    verify(this.vehicleUpdateRepository, never()).updateVehicle(any());
  }

  @Test
  public void shouldCreateVehicleWhenUpdaterValidAndNotFoundDataBase() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.LIVE, System.currentTimeMillis(), "updater", true) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        element.setModel("model");
        return element;
      }
    };

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(List.of());
    when(this.vehicleFactory.create(eq(id), eq(storeId), any())).thenReturn(new CarDO(id, storeId, 1, 1L));

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    final ArgumentCaptor<VehicleDO> argumentCaptor = ArgumentCaptor.forClass(VehicleDO.class);
    verify(this.vehicleUpdateRepository).updateVehicle(argumentCaptor.capture());
    final VehicleDO result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(1));
    assertThat(result.getUpdateInstant("updater"), is(notNullValue()));
    assertThat(result.getUpdateInstant("updater").isBefore(Instant.now()), is(true));
    assertThat(result.getModel(), is("model"));
  }

  @Test
  public void shouldUpdateVehicleWhenUpdaterLiveAndFoundDataBaseAndAfterLastUpdate() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.LIVE, System.currentTimeMillis(), "updater", true) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        element.setModel("model");
        return element;
      }
    };
    final CarDO carDO = new CarDO(id, storeId, 1, 1L);
    carDO.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().minus(1, ChronoUnit.DAYS))));

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(new ArrayList<>(List.of(carDO)));

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    final ArgumentCaptor<VehicleDO> argumentCaptor = ArgumentCaptor.forClass(VehicleDO.class);
    verify(this.vehicleUpdateRepository).updateVehicle(argumentCaptor.capture());
    final VehicleDO result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(1));
    assertThat(result.getUpdateInstant("updater"), is(notNullValue()));
    assertThat(result.getUpdateInstant("updater").isBefore(Instant.now()), is(true));
    assertThat(result.getModel(), is("model"));
  }

  @Test
  public void shouldUpdateVehicleWhenUpdaterPreviewAndFoundDataBaseAndAfterLastUpdate() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.PREVIEW, System.currentTimeMillis(), "updater", true) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        element.setModel("model");
        return element;
      }
    };
    final CarDO carDO = new CarDO(id, storeId, 1, 1L);
    carDO.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().minus(1, ChronoUnit.DAYS))));

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(new ArrayList<>(List.of(carDO)));
    when(this.vehicleFactory.clone(eq(carDO), any())).thenReturn(carDO.toBuilder().vehicleVersion(2).build());

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    verify(this.vehicleUpdateRepository).delete(List.of());
    final ArgumentCaptor<VehicleDO> argumentCaptor = ArgumentCaptor.forClass(VehicleDO.class);
    verify(this.vehicleUpdateRepository).updateVehicle(argumentCaptor.capture());
    final VehicleDO result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(2));
    assertThat(result.getUpdateInstant("updater"), is(notNullValue()));
    assertThat(result.getUpdateInstant("updater").isBefore(Instant.now()), is(true));
    assertThat(result.getModel(), is("model"));
  }

  @Test
  public void notShouldUpdateVehicleWhenUpdaterPreviewAndFoundDataBaseAndBeforeLastUpdate() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.PREVIEW, System.currentTimeMillis(), "updater", true) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        element.setModel("model");
        return element;
      }
    };
    final CarDO carDO = new CarDO(id, storeId, 1, 1L);
    carDO.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().plus(1, ChronoUnit.DAYS))));

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId))).thenReturn(new ArrayList<>(List.of(carDO)));
    when(this.vehicleFactory.clone(eq(carDO), any())).thenReturn(carDO.toBuilder().vehicleVersion(2).build());

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    verify(this.vehicleUpdateRepository, never()).updateVehicle(any());
  }

  @Test
  public void shouldUpdateVehicleAndTwoElementsAndFoundDataBaseAndBeforeLastUpdate() {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";
    final VehicleUpdater updater = new VehicleUpdater(UpdaterType.LIVE, System.currentTimeMillis(), "updater", true) {
      @Override
      public VehicleDO update(final VehicleDO element) {
        element.setModel("model");
        return element;
      }
    };
    final CarDO carDO = new CarDO(id, storeId, 1, 1L);
    carDO.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().minus(3, ChronoUnit.DAYS))));
    carDO.setUpdateVersion(1L);
    final CarDO carDO2 = new CarDO(id, storeId, 2, 2L);
    carDO2.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().minus(2, ChronoUnit.DAYS))));
    carDO2.setUpdateVersion(2L);
    final CarDO carDO3 = new CarDO(id, storeId, 3, 3L);
    carDO3.setUpdateInstants(new HashMap<>(Map.of("updater", Instant.now().minus(1, ChronoUnit.DAYS))));
    carDO3.setUpdateVersion(3L);

    when(this.vehicleUpdateRepository.findVehiclesByStoreId(eq(id), eq(storeId)))
        .thenReturn(new ArrayList<>(List.of(carDO, carDO2, carDO3)));
    when(this.vehicleFactory.clone(any(), any())).thenReturn(carDO2);

    //When
    this.vehicleUpdateService.update(id, storeId, updater);

    //Then
    verify(this.vehicleUpdateRepository).delete(List.of(carDO2, carDO));
    final ArgumentCaptor<VehicleDO> argumentCaptor = ArgumentCaptor.forClass(VehicleDO.class);
    verify(this.vehicleUpdateRepository).updateVehicle(argumentCaptor.capture());
    final VehicleDO result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(3));
    assertThat(result.getUpdateInstant("updater"), is(notNullValue()));
    assertThat(result.getUpdateInstant("updater").isBefore(Instant.now()), is(true));
    assertThat(result.getModel(), is("model"));
  }

}