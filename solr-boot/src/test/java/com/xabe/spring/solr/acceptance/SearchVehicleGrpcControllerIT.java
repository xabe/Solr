package com.xabe.spring.solr.acceptance;

import static com.xabe.spring.solr.acceptance.util.LoadData.BMW;
import static com.xabe.spring.solr.acceptance.util.LoadData.MAZDA;
import static com.xabe.spring.solr.acceptance.util.LoadData.TRUCK_MERCEDES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.solr.acceptance.util.GrpcUtil;
import com.xabe.spring.solr.acceptance.util.LoadData;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest.SectionDefinition;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleResponseOuterClass.GetVehicleResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchVehicleGrpcControllerIT extends AbstractVehicleGrpc {

  @Autowired
  private LoadData loadData;

  @BeforeAll
  public void setUp() throws Exception {
    this.loadData.init("sp");
  }

  @Test
  public void givenAGetVehicleRequestWithFilterPricesWhenInvokeGetVehicleThenReturnGetVehicleResponse() throws Exception {
    //Given
    final GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setStoreId("sp").setLocale("es-ES").setVisibilityVersion(System.currentTimeMillis())
            .setFilterHasPrices(true)
            .addAllSections(List.of(SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("vehicleId:" + MAZDA).build()))
            .build();

    //When
    final GetVehicleResponse result = GrpcUtil.instance().getSearchVehicleServiceBlockingStub().getVehicle(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(1));
    assertThat(result.getSections(0).getElementsCount(), is(1));
    assertThat(result.getSections(0).getElementsList().get(0).getCar().getVehicleId().getId(), is(MAZDA));
  }

  @Test
  public void givenAGetVehicleRequestWithoutFilterPricesWhenInvokeGetVehicleThenReturnGetVehicleResponse() throws Exception {
    //Given
    final GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setStoreId("sp").setLocale("es-ES").setVisibilityVersion(System.currentTimeMillis())
            .setFilterHasPrices(false)
            .addAllSections(List.of(SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("tags: TRAILER").build()))
            .build();

    //When
    final GetVehicleResponse result = GrpcUtil.instance().getSearchVehicleServiceBlockingStub().getVehicle(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(1));
    assertThat(result.getSections(0).getElementsCount(), is(1));
    assertThat(result.getSections(0).getElementsList().get(0).getTruck().getVehicleId().getId(), is(TRUCK_MERCEDES));
  }

  @Test
  public void givenAGetVehicleRequestWhenInvokeGetVehicleThenReturnGetVehicleResponse() throws Exception {
    //Given
    final GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setStoreId("sp").setLocale("es-ES").setVisibilityVersion(System.currentTimeMillis())
            .setFilterHasPrices(false)
            .addAllSections(List.of(
                SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("tags: TRAILER").build(),
                SectionDefinition.newBuilder().setSequenceNo(1).setFilterQuery("HATCHBACK").build()))
            .build();

    //When
    final GetVehicleResponse result = GrpcUtil.instance().getSearchVehicleServiceBlockingStub().getVehicle(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(2));
    assertThat(result.getSections(0).getElementsCount(), is(1));
    assertThat(result.getSections(0).getElementsList().get(0).getTruck().getVehicleId().getId(), is(TRUCK_MERCEDES));
    assertThat(result.getSections(1).getElementsCount(), is(2));
    assertThat(result.getSections(1).getElementsList().get(0).getCar().getVehicleId().getId(), is(MAZDA));
    assertThat(result.getSections(1).getElementsList().get(1).getCar().getVehicleId().getId(), is(BMW));
  }

  @Test
  public void givenAGetVehicleRequestCarWhenInvokeGetVehicleThenReturnGetVehicleResponse() throws Exception {
    //Given
    final GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setStoreId("sp").setLocale("en-GB").setVisibilityVersion(System.currentTimeMillis())
            .setFilterHasPrices(true)
            .addAllSections(List.of(
                SectionDefinition.newBuilder().setSequenceNo(0).setFilterQuery("SEDAN").build(),
                SectionDefinition.newBuilder().setSequenceNo(1).setFilterQuery("TRAILER").build()))
            .build();

    //When
    final GetVehicleResponse result = GrpcUtil.instance().getSearchVehicleServiceBlockingStub().getVehicle(request);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getSectionsCount(), is(2));
    assertThat(result.getSections(0).getElementsCount(), is(2));
    assertThat(result.getSections(0).getElementsList().get(0).getCar().getVehicleId().getId(), is(MAZDA));
    assertThat(result.getSections(0).getElementsList().get(1).getCar().getVehicleId().getId(), is(BMW));
    assertThat(result.getSections(1).getElementsCount(), is(0));
  }
}
