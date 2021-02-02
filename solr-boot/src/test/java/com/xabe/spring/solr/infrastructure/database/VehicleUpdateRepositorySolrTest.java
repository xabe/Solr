package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.exception.RepositoryException;
import com.xabe.spring.solr.domain.exception.VehicleConcurrentUpdateException;
import com.xabe.spring.solr.domain.repository.update.VehicleUpdateRepository;
import com.xabe.spring.solr.infrastructure.database.converter.VehicleConverter;
import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class VehicleUpdateRepositorySolrTest {

  private Logger logger;

  private SolrClient solrClient;

  private VehicleConverter vehicleConverter;

  private VehicleUpdateRepository vehicleUpdateRepository;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.solrClient = mock(SolrClient.class);
    this.vehicleConverter = mock(VehicleConverter.class);
    when(this.vehicleConverter.getType()).thenReturn(VehicleTypeDO.CAR);
    this.vehicleUpdateRepository = new VehicleUpdateRepositorySolr(10, this.solrClient, this.logger, List.of(this.vehicleConverter));
  }

  @Test
  public void shouldDeleteVehicle() throws Exception {
    //Given
    final List<VehicleDO> vehicles = List.of(
        CarDO.builder().id(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build()).storeId("sp").vehicleVersion(1).build());

    //When
    this.vehicleUpdateRepository.delete(vehicles);

    //Then
    final ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(this.solrClient).deleteById(argumentCaptor.capture());
    final List<String> result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(1)));
    assertThat(result, is(hasItem("sp!CAR::id::1")));
  }

  @Test
  public void notShouldDeleteVehicleIsEmptyList() throws Exception {
    //Given
    final List<VehicleDO> vehicles = List.of();

    //When
    this.vehicleUpdateRepository.delete(vehicles);

    //Then
    verify(this.solrClient, never()).deleteById(anyList());
  }

  @Test
  public void shouldUpdateVehicle() throws Exception {
    //Given
    final VehicleDO vehicle =
        CarDO.builder().id(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build()).storeId("sp").vehicleVersion(1).build();

    when(this.vehicleConverter.writeToSolr(eq(vehicle))).thenReturn(new SolrInputDocument());

    //When
    this.vehicleUpdateRepository.updateVehicle(vehicle);

    //Then
    verify(this.solrClient).add(any(SolrInputDocument.class));
  }

  @Test
  public void shouldThrowVehicleConcurrentUpdateException() throws Exception {
    //Given
    final VehicleDO vehicle =
        CarDO.builder().id(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build()).storeId("sp").vehicleVersion(1).build();

    when(this.vehicleConverter.writeToSolr(eq(vehicle))).thenReturn(new SolrInputDocument());
    when(this.solrClient.add(any(SolrInputDocument.class))).thenThrow(new SolrException(ErrorCode.CONFLICT, "error"));

    //When
    Assertions.assertThrows(VehicleConcurrentUpdateException.class, () -> this.vehicleUpdateRepository.updateVehicle(vehicle));
  }

  @Test
  public void shouldThrowRepositoryException() throws Exception {
    //Given
    final VehicleDO vehicle =
        CarDO.builder().id(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build()).storeId("sp").vehicleVersion(1).build();

    when(this.vehicleConverter.writeToSolr(eq(vehicle))).thenReturn(new SolrInputDocument());
    when(this.solrClient.add(any(SolrInputDocument.class))).thenThrow(new SolrException(ErrorCode.BAD_REQUEST, "error"));

    //When
    Assertions.assertThrows(RepositoryException.class, () -> this.vehicleUpdateRepository.updateVehicle(vehicle));
  }

  @Test
  public void shouldThrowRepositoryExceptionAnyException() throws Exception {
    //Given
    final VehicleDO vehicle =
        CarDO.builder().id(VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build()).storeId("sp").vehicleVersion(1).build();

    when(this.vehicleConverter.writeToSolr(eq(vehicle))).thenReturn(new SolrInputDocument());
    when(this.solrClient.add(any(SolrInputDocument.class))).thenThrow(new SolrServerException(null, null));

    //When
    Assertions.assertThrows(RepositoryException.class, () -> this.vehicleUpdateRepository.updateVehicle(vehicle));
  }

  @Test
  public void shouldFindVehiclesByStoreIdWhenNotFoundSolr() throws Exception {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";

    final QueryResponse solrQueryResponse = this.createSolrQueryResponse(true);
    final ArgumentCaptor<SolrQuery> argumentCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    when(this.solrClient.query(argumentCaptor.capture(), eq(METHOD.POST))).thenReturn(solrQueryResponse).thenReturn(solrQueryResponse);

    //When
    final List<VehicleDO> result = this.vehicleUpdateRepository.findVehiclesByStoreId(id, storeId);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(0)));
    final List<SolrQuery> solrQueries = argumentCaptor.getAllValues();
    assertThat(solrQueries.get(0).get("fl"), is(VEHICLE_VERSION));
    assertThat(solrQueries.get(0).get("fq"), is("storeId:(sp)"));
    assertThat(solrQueries.get(0).get("q"), is(containsString("{!cache=false}")));
    assertThat(solrQueries.get(0).get("q"), is(containsString("(vehicleId:id AND vehicleType:CAR)")));
    assertThat(solrQueries.get(0).get("_route_"), is("sp!"));
    assertThat(solrQueries.get(1).get("fl"), is("*"));
    assertThat(solrQueries.get(1).getRequestHandler(), is("/get"));
    assertThat(solrQueries.get(1).getParams("id")[0], is("sp!CAR::id::1"));
    assertThat(solrQueries.get(1).getParams("id")[1], is("sp!THIS_WILL_NEVER_MATCH"));
  }

  @Test
  public void shouldFindVehiclesByStoreIdWhenFoundSolr() throws Exception {
    //Given
    final VehicleIdDO id = VehicleIdDO.builder().id("id").type(VehicleTypeDO.CAR).build();
    final String storeId = "sp";

    final QueryResponse solrQueryResponse = this.createSolrQueryResponse(false);
    final ArgumentCaptor<SolrQuery> argumentCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    when(this.solrClient.query(argumentCaptor.capture(), eq(METHOD.POST))).thenReturn(solrQueryResponse).thenReturn(solrQueryResponse);
    when(this.vehicleConverter.readerFromSolr(any())).thenReturn(CarDO.builder().build());

    //When
    final List<VehicleDO> result = this.vehicleUpdateRepository.findVehiclesByStoreId(id, storeId);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(1)));
    final List<SolrQuery> solrQueries = argumentCaptor.getAllValues();
    assertThat(solrQueries.get(0).get("fl"), is(VEHICLE_VERSION));
    assertThat(solrQueries.get(0).get("fq"), is("storeId:(sp)"));
    assertThat(solrQueries.get(0).get("q"), is(containsString("{!cache=false}")));
    assertThat(solrQueries.get(0).get("q"), is(containsString("(vehicleId:id AND vehicleType:CAR)")));
    assertThat(solrQueries.get(0).get("_route_"), is("sp!"));
    assertThat(solrQueries.get(1).get("fl"), is("*"));
    assertThat(solrQueries.get(1).getRequestHandler(), is("/get"));
    assertThat(solrQueries.get(1).getParams("id")[0], is("sp!CAR::id::0"));
    assertThat(solrQueries.get(1).getParams("id")[1], is("sp!CAR::id::1"));
    assertThat(solrQueries.get(1).getParams("id")[2], is("sp!THIS_WILL_NEVER_MATCH"));
  }

  private QueryResponse createSolrQueryResponse(final boolean empty) {
    final SolrDocumentList documents = new SolrDocumentList();
    if (!empty) {
      final SolrDocument document = new SolrDocument();
      document.setField(VEHICLE_VERSION, 0);
      document.setField(VEHICLE_TYPE, "CAR");
      documents.add(document);
    }
    final NamedList namedList = new NamedList();
    namedList.add("response", documents);
    final QueryResponse response = new QueryResponse();
    response.setResponse(namedList);
    return response;
  }
}