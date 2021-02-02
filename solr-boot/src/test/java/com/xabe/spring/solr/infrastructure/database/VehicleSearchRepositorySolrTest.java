package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.TAGS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ALL_FIELDS;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.DF;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.FL;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ROUTE;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ROUTE_ID_SEPARATOR;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.BRACKET_CLOSE;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.BRACKET_OPEN;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.COLLAPSE_FQ;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.OPERATOR_AND;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.OPERATOR_AND_NOT;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.OPERATOR_OR;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.QUERY_HAS_PRICES;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.QUERY_STORE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.VEHICLE_TYPE_QUERY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.exception.RepositoryException;
import com.xabe.spring.solr.domain.repository.search.VehicleSearchRepository;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDefinitionDTO;
import com.xabe.spring.solr.infrastructure.database.converter.VehicleConverter;
import com.xabe.spring.solr.infrastructure.database.parser.QueryTranslator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class VehicleSearchRepositorySolrTest {

  private QueryTranslator translator;

  private SolrClient solrClient;

  private VehicleConverter vehicleConverter;

  private VehicleSearchRepository vehicleSearchRepository;

  @BeforeEach
  public void setUp() throws Exception {
    this.solrClient = mock(SolrClient.class);
    this.vehicleConverter = mock(VehicleConverter.class);
    this.translator = mock(QueryTranslator.class);
    when(this.translator.translate(anyString())).then(AdditionalAnswers.returnsFirstArg());
    when(this.vehicleConverter.getType()).thenReturn(VehicleTypeDO.CAR);
    this.vehicleSearchRepository =
        new VehicleSearchRepositorySolr(100, this.translator, this.solrClient, mock(Logger.class), List.of(this.vehicleConverter));
  }

  @Test
  public void givenAFilterAndSectionAndExclusionsEmptyWhenInvokeFindVehicleBySectionDefinitionThenReturnListVehicleDO()
      throws Exception {
    final String filter = "tags: (shirt)";
    final SectionDefinitionDTO sectionDefinition = SectionDefinitionDTO.builder().sequenceNo(0).filterQuery("prices: [* TO 100]").build();
    final List<SectionDefinitionDTO> excludeSections = List.of();
    final ParamsDTO params = ParamsDTO.builder().filterHasPrice(false).storeId("storeId").build();

    final ArgumentCaptor<SolrQuery> argumentCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    final QueryResponse response = VehicleSearchRepositorySolrMother.createSolrQueryResponse("CAR");
    when(this.solrClient.query(argumentCaptor.capture(), eq(METHOD.POST))).thenReturn(response);

    final List<VehicleDO> result =
        this.vehicleSearchRepository.findVehicleBySectionDefinition(filter, sectionDefinition, excludeSections, params);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(1)));
    verify(this.vehicleConverter).readerFromSolr(eq(response.getResults().get(0)));
    final SolrQuery solrQuery = argumentCaptor.getValue();

    this.assertSectionQuery(solrQuery, filter, sectionDefinition, excludeSections);
    this.assertSectionFilterQueries(solrQuery, params);
    assertThat(solrQuery.get(DF), is(TAGS));
    assertThat(solrQuery.get(ROUTE), is(params.getStoreId() + ROUTE_ID_SEPARATOR));
    assertThat(solrQuery.getRows(), is(100));
    assertThat(solrQuery.get(FL), is(ALL_FIELDS));
    assertThat(solrQuery.getSorts().get(0).getItem(), is(VEHICLE_ID));
    assertThat(solrQuery.getSorts().get(0).getOrder(), is(ORDER.desc));
  }

  @Test
  public void givenAFilterAndSectionAndExclusionsAndFiltersWhenInvokeFindVehicleBySectionDefinitionThenReturnListVehicleDO()
      throws Exception {
    final String filter = "tags: shirt";
    final SectionDefinitionDTO sectionDefinition = SectionDefinitionDTO.builder().sequenceNo(0).filterQuery("prices: [* TO 100]").build();
    final List<SectionDefinitionDTO> excludeSections = List.of(SectionDefinitionDTO.builder().filterQuery("tags: SUV").build());
    final ParamsDTO params = ParamsDTO.builder().filterHasPrice(true).storeId("storeId").build();

    final ArgumentCaptor<SolrQuery> argumentCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    final QueryResponse response = VehicleSearchRepositorySolrMother.createSolrQueryResponse("CAR");
    when(this.solrClient.query(argumentCaptor.capture(), eq(METHOD.POST))).thenReturn(response);

    final List<VehicleDO> result =
        this.vehicleSearchRepository.findVehicleBySectionDefinition(filter, sectionDefinition, excludeSections, params);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(1)));
    verify(this.vehicleConverter).readerFromSolr(eq(response.getResults().get(0)));
    final SolrQuery solrQuery = argumentCaptor.getValue();

    this.assertSectionQuery(solrQuery, filter, sectionDefinition, excludeSections);
    this.assertSectionFilterQueries(solrQuery, params);
    assertThat(solrQuery.get(DF), is(TAGS));
    assertThat(solrQuery.get(ROUTE), is(params.getStoreId() + ROUTE_ID_SEPARATOR));
    assertThat(solrQuery.getRows(), is(100));
    assertThat(solrQuery.get(FL), is(ALL_FIELDS));
    assertThat(solrQuery.getSorts().get(0).getItem(), is(VEHICLE_ID));
    assertThat(solrQuery.getSorts().get(0).getOrder(), is(ORDER.desc));
  }

  @Test
  public void givenAFilterAndSectionAndExclusionsAndFiltersWhenInvokeFindVehicleBySectionDefinitionThenReturnException()
      throws Exception {
    final String filter = "tags: shirt";
    final SectionDefinitionDTO sectionDefinition = SectionDefinitionDTO.builder().sequenceNo(0).filterQuery("prices: [* TO 100]").build();
    final List<SectionDefinitionDTO> excludeSections = List.of(SectionDefinitionDTO.builder().filterQuery("tags: SUV").build());
    final ParamsDTO params = ParamsDTO.builder().filterHasPrice(true).storeId("storeId").build();

    final ArgumentCaptor<SolrQuery> argumentCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    final QueryResponse response = VehicleSearchRepositorySolrMother.createSolrQueryResponse("TRUCK");
    when(this.solrClient.query(argumentCaptor.capture(), eq(METHOD.POST))).thenReturn(response);

    Assertions.assertThrows(
        RepositoryException.class,
        () -> this.vehicleSearchRepository.findVehicleBySectionDefinition(filter, sectionDefinition, excludeSections, params));
  }

  private void assertSectionQuery(final SolrQuery query, final String filter, final SectionDefinitionDTO section,
      final List<SectionDefinitionDTO> excludedFilters) {
    final StringBuilder stringQuery = new StringBuilder();

    if (StringUtils.isNotBlank(filter)) {
      stringQuery.append(filter).append(OPERATOR_AND);
    }

    stringQuery.append(BRACKET_OPEN);
    stringQuery.append(this.createAssertableQuery(section));

    if (CollectionUtils.isNotEmpty(excludedFilters)) {
      stringQuery.append(OPERATOR_AND_NOT);
      stringQuery.append(BRACKET_OPEN);
      stringQuery.append(excludedFilters.stream().map(this::createAssertableQuery).collect(Collectors.joining(OPERATOR_OR)));
      stringQuery.append(BRACKET_CLOSE);
    }

    stringQuery.append(BRACKET_CLOSE);
    stringQuery.append(VEHICLE_TYPE_QUERY);

    assertThat(query.getQuery(), is(stringQuery.toString()));
  }

  private String createAssertableQuery(final SectionDefinitionDTO sectionDefinition) {
    final StringBuilder query = new StringBuilder();
    query.append(BRACKET_OPEN).append(sectionDefinition.getFilterQuery()).append(BRACKET_CLOSE);
    return query.toString();
  }

  private void assertSectionFilterQueries(final SolrQuery query, final ParamsDTO params) {
    final List<String> filterQueries = new ArrayList<>(10);

    if (params.isFilterHasPrice()) {
      filterQueries.add(QUERY_HAS_PRICES);
    }

    final Set<String> expectedQueries = new HashSet<>(3);

    expectedQueries.add(filterQueries.stream().collect(Collectors.joining(OPERATOR_AND)));
    expectedQueries.add(QUERY_STORE_ID.apply(params.getStoreId()));
    expectedQueries.add(COLLAPSE_FQ);

    assertThat(Stream.of(query.getFilterQueries()).collect(Collectors.toSet()), is(expectedQueries));
  }

}