package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.TAGS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ALL_FIELDS;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.DF;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.FL;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.Q;
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

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.exception.RepositoryException;
import com.xabe.spring.solr.domain.repository.search.VehicleSearchRepository;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDefinitionDTO;
import com.xabe.spring.solr.infrastructure.database.converter.VehicleConverter;
import com.xabe.spring.solr.infrastructure.database.parser.QueryTranslator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleSearchRepositorySolr implements VehicleSearchRepository {

  private static final METHOD SOLR_DEFAULT_METHOD = METHOD.POST;

  private static final String ERROR_IN_FETCHING_VEHICLE_FROM_SOL_R = "Error in fetching vehicle from SolR";

  private final Integer rowCount;

  private final SolrClient solrClient;

  private final Logger logger;

  private final QueryTranslator translator;

  private final Map<VehicleTypeDO, VehicleConverter> vehicleConverterMap;

  @Autowired
  public VehicleSearchRepositorySolr(@Value("${db.vehicle.row-count:1000}") final Integer rowCount, final QueryTranslator translator,
      final SolrClient solrClient, final Logger logger, final List<VehicleConverter> vehicleConverters) {
    this.rowCount = rowCount;
    this.translator = translator;
    this.solrClient = solrClient;
    this.logger = logger;
    this.vehicleConverterMap = vehicleConverters.stream().collect(Collectors.toMap(VehicleConverter::getType, Function.identity()));
  }

  @Override
  public List<VehicleDO> findVehicleBySectionDefinition(final String filter, final SectionDefinitionDTO sectionDefinition,
      final List<SectionDefinitionDTO> excludedSections, final ParamsDTO params) {
    final SolrQuery solrQuery = new SolrQuery();

    solrQuery.set(Q, this.createQueryWithExclusion(filter, sectionDefinition, excludedSections));

    solrQuery.addFilterQuery(String.join(OPERATOR_AND, this.createFilterQueries(params)));
    solrQuery.addFilterQuery(COLLAPSE_FQ);
    solrQuery.addFilterQuery(QUERY_STORE_ID.apply(params.getStoreId()));

    solrQuery.set(DF, TAGS);
    solrQuery.set(ROUTE, params.getStoreId() + ROUTE_ID_SEPARATOR);
    solrQuery.setRows(this.rowCount);

    solrQuery.set(FL, ALL_FIELDS);

    // We must add a deterministic sorting in every use case to assure the same outcome
    solrQuery.addSort(VEHICLE_ID, ORDER.desc);
    try {
      final QueryResponse response = this.solrClient.query(solrQuery, SOLR_DEFAULT_METHOD);
      final SolrDocumentList results = response.getResults();
      return results.stream().map(this::<VehicleDO>mapSolrToGridElementDO).collect(Collectors.toList());
    } catch (final Exception ex) {
      this.logger.error(ERROR_IN_FETCHING_VEHICLE_FROM_SOL_R, ex);
      throw new RepositoryException(ex);
    }
  }

  private String createQueryWithExclusion(final String filter, final SectionDefinitionDTO sectionDefinition,
      final List<SectionDefinitionDTO> excludedSections) {
    final StringBuilder sb = new StringBuilder();

    if (StringUtils.isNotBlank(filter)) {
      sb.append(this.translator.translate(filter)).append(OPERATOR_AND);
    }

    sb.append(BRACKET_OPEN);
    sb.append(this.createQuery(sectionDefinition));

    if (CollectionUtils.isNotEmpty(excludedSections)) {
      sb.append(OPERATOR_AND_NOT);
      sb.append(BRACKET_OPEN);
      sb.append(excludedSections.stream().map(this::createQuery).collect(Collectors.joining(OPERATOR_OR)));
      sb.append(BRACKET_CLOSE);
    }
    sb.append(BRACKET_CLOSE);

    return sb.append(VEHICLE_TYPE_QUERY).toString();
  }

  private String createQuery(final SectionDefinitionDTO sectionDefinition) {
    final StringBuilder sb = new StringBuilder();
    sb.append(BRACKET_OPEN).append(this.translator.translate(sectionDefinition.getFilterQuery())).append(BRACKET_CLOSE);
    return sb.toString();
  }

  private List<String> createFilterQueries(final ParamsDTO params) {
    final List<String> filterQueries = new ArrayList<>();

    this.addFilterQuery(params::isFilterHasPrice, filterQueries, () -> QUERY_HAS_PRICES);

    return filterQueries;
  }

  private <T> void addFilterQuery(final Supplier<Boolean> test, final List<T> list, final Supplier<T> value) {
    if (test.get()) {
      list.add(value.get());
    }
  }

  private <T extends VehicleDO> T mapSolrToGridElementDO(final SolrDocument solrDocument) {
    final VehicleTypeDO vehicleTypeDO = VehicleTypeDO.getType(solrDocument.getFieldValue(VEHICLE_TYPE).toString());
    final VehicleConverter<T> gridElementFactory = this.vehicleConverterMap.get(vehicleTypeDO);
    return gridElementFactory.readerFromSolr(solrDocument);
  }
}
