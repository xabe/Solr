package com.xabe.spring.solr.infrastructure.application.update.service.factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleFactoryImplTest {

  private VehicleFactory vehicleFactory;

  @BeforeEach
  public void setUp() throws Exception {
    this.vehicleFactory = new VehicleFactoryImpl();
  }

  @Test
  public void shouldCreteCar() throws Exception {
    //Given
    final VehicleIdDO vehicleIdDO = VehicleIdDO.builder().type(VehicleTypeDO.CAR).id("id").build();
    final String storeId = "sp";

    //When
    final VehicleDO result = this.vehicleFactory.create(vehicleIdDO, storeId, Instant.now());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(1));
    assertThat(result.getUpdateVersion(), is(-1L));
    assertThat(result.getVisibilityVersionTimestamp(), is(notNullValue()));
  }

  @Test
  public void shouldCloneCar() throws Exception {
    //Given
    final VehicleIdDO vehicleIdDO = VehicleIdDO.builder().type(VehicleTypeDO.CAR).id("id").build();
    final String storeId = "sp";
    final VehicleDO vehicleDO =
        CarDO.builder().id(vehicleIdDO).storeId(storeId).updateInstants(Map.of()).vehicleVersion(1).updateVersion(100L).build();

    //When
    final VehicleDO result = this.vehicleFactory.clone(vehicleDO, Instant.now());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.CAR));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(2));
    assertThat(result.getUpdateVersion(), is(-1L));
    assertThat(result.getVisibilityVersionTimestamp(), is(notNullValue()));
  }

  @Test
  public void shouldCreteTruck() throws Exception {
    //Given
    final VehicleIdDO vehicleIdDO = VehicleIdDO.builder().type(VehicleTypeDO.TRUCK).id("id").build();
    final String storeId = "sp";

    //When
    final VehicleDO result = this.vehicleFactory.create(vehicleIdDO, storeId, Instant.now());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.TRUCK));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(1));
    assertThat(result.getUpdateVersion(), is(-1L));
    assertThat(result.getVisibilityVersionTimestamp(), is(notNullValue()));
  }

  @Test
  public void shouldCloneTruck() throws Exception {
    //Given
    final VehicleIdDO vehicleIdDO = VehicleIdDO.builder().type(VehicleTypeDO.TRUCK).id("id").build();
    final String storeId = "sp";
    final VehicleDO vehicleDO =
        TruckDO.builder().id(vehicleIdDO).storeId(storeId).updateInstants(Map.of()).vehicleVersion(1).updateVersion(100L).build();

    //When
    final VehicleDO result = this.vehicleFactory.clone(vehicleDO, Instant.now());

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(notNullValue()));
    assertThat(result.getId().getId(), is("id"));
    assertThat(result.getId().getType(), is(VehicleTypeDO.TRUCK));
    assertThat(result.getStoreId(), is("sp"));
    assertThat(result.getVehicleVersion(), is(2));
    assertThat(result.getUpdateVersion(), is(-1L));
    assertThat(result.getVisibilityVersionTimestamp(), is(notNullValue()));
  }

}