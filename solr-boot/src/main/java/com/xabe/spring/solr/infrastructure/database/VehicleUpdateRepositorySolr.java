package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ALL_FIELDS;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.CACHE_QUERY;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.FL;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.FQ;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ID_SEPARATOR;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.PATH_GET;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.Q;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ROUTE;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ROUTE_ID_SEPARATOR;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.QUERY_STORE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.QUERY_VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrQueries.THIS_WILL_NEVER_MATCH;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.exception.RepositoryException;
import com.xabe.spring.solr.domain.exception.VehicleConcurrentUpdateException;
import com.xabe.spring.solr.domain.repository.update.VehicleUpdateRepository;
import com.xabe.spring.solr.infrastructure.database.converter.VehicleConverter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleUpdateRepositorySolr implements VehicleUpdateRepository {

  private static final METHOD SOLR_DEFAULT_METHOD = METHOD.POST;

  private static final String ERROR_IN_FETCHING_VEHICLE_FROM_SOL_R = "Error in fetching vehicle from SolR";

  private final Integer rowCount;

  private final SolrClient solrClient;

  private final Logger logger;

  private final Map<VehicleTypeDO, VehicleConverter> vehicleConverterMap;

  @Autowired
  public VehicleUpdateRepositorySolr(@Value("${db.vehicle.row-count:1000}") final Integer rowCount,
      final SolrClient solrClient, final Logger logger, final List<VehicleConverter> vehicleConverters) {
    this.rowCount = rowCount;
    this.solrClient = solrClient;
    this.logger = logger;
    this.vehicleConverterMap = vehicleConverters.stream().collect(Collectors.toMap(VehicleConverter::getType, Function.identity()));
  }

  @Override
  public List<VehicleDO> findVehiclesByStoreId(final VehicleIdDO id, final String storeId) {
    final List<Integer> gridElementsVersions = this.findVehicleLastVersion(id, storeId);
    if (CollectionUtils.isEmpty(gridElementsVersions)) {
      gridElementsVersions.add(1);
    } else {
      gridElementsVersions.sort(Comparator.reverseOrder());
      gridElementsVersions.add(gridElementsVersions.get(0) + 1);
    }
    return this.getRealTimeCommComponents(id, storeId, gridElementsVersions);
  }

  private List<Integer> findVehicleLastVersion(final VehicleIdDO id, final String storeId) {
    final SolrQuery solrQuery = new SolrQuery();

    final StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(CACHE_QUERY).append(QUERY_VEHICLE_ID.apply(id.getId(), id.getType().name()));
    solrQuery.set(Q, queryBuilder.toString());
    solrQuery.set(FQ, QUERY_STORE_ID.apply(storeId));
    solrQuery.set(FL, VEHICLE_VERSION);
    solrQuery.set(ROUTE, storeId + ROUTE_ID_SEPARATOR);
    solrQuery.setRows(this.rowCount);

    try {
      final QueryResponse response = this.solrClient.query(solrQuery, SOLR_DEFAULT_METHOD);
      return response.getResults().stream().filter(doc -> Objects.nonNull(doc.getFieldValue(VEHICLE_VERSION)))
          .map(doc -> (Integer) doc.getFieldValue(VEHICLE_VERSION)).collect(Collectors.toList());
    } catch (final Exception ex) {
      this.logger.error(ERROR_IN_FETCHING_VEHICLE_FROM_SOL_R, ex);
      throw new RepositoryException(ex);
    }
  }

  private <T extends VehicleDO> List<T> getRealTimeCommComponents(final VehicleIdDO id, final String storeId,
      final List<Integer> latestVersions) {
    final SolrQuery solrQuery = new SolrQuery();

    try {
      solrQuery.setRequestHandler(PATH_GET);
      latestVersions.stream().forEach(version -> solrQuery.add(ID, this.constructVehicleSolrId(id, storeId, version)));
      solrQuery.set(ROUTE, storeId + ROUTE_ID_SEPARATOR);
      // two hacks in one line:
      // - adding an additional ID forces solr to use a "multi document" response instead of a weird response format
      // - using the storeId!ID_WONT_MATCH is to avoid routing problems while fetching the documents
      solrQuery.add(ID, storeId + ROUTE_ID_SEPARATOR + THIS_WILL_NEVER_MATCH);
      solrQuery.set(FL, ALL_FIELDS);
      final QueryResponse queryResponse = this.solrClient.query(solrQuery, SOLR_DEFAULT_METHOD);

      return queryResponse.getResults().stream().map(this::<T>mapSolrToGridElementDO).collect(Collectors.toList());
    } catch (final Exception ex) {
      this.logger.error(ERROR_IN_FETCHING_VEHICLE_FROM_SOL_R, ex);
      throw new RepositoryException(ex);
    }
  }

  private <T extends VehicleDO> T mapSolrToGridElementDO(final SolrDocument solrDocument) {
    final VehicleTypeDO vehicleTypeDO = VehicleTypeDO.getType(solrDocument.getFieldValue(VEHICLE_TYPE).toString());
    final VehicleConverter<T> gridElementFactory = this.vehicleConverterMap.get(vehicleTypeDO);
    return gridElementFactory.readerFromSolr(solrDocument);
  }

  @Override
  public void updateVehicle(final VehicleDO vehicle) {
    try {
      this.solrClient.add(this.mapDocumentSolr(vehicle));
    } catch (final SolrException e) {
      if (e.code() == SolrException.ErrorCode.CONFLICT.code) {
        this.logger.error("Failed to update, there is a version more recent VehicleDocument {} to Solr", vehicle, e);
        throw new VehicleConcurrentUpdateException(e.getMessage());
      } else {
        this.logger.error("Failed to update VehicleDocument {} to Solr", vehicle, e);
        throw new RepositoryException(e);
      }
    } catch (final Exception e) {
      this.logger.error("Failed to upsert document for VehicleId {} to Solr", vehicle, e);
      throw new RepositoryException(e);
    }
  }

  private SolrInputDocument mapDocumentSolr(final VehicleDO gridElement) {
    final VehicleTypeDO type = gridElement.getId().getType();
    return this.vehicleConverterMap.get(type).writeToSolr(gridElement);
  }

  @Override
  public void delete(final List<VehicleDO> vehicles) {
    try {
      if (CollectionUtils.isNotEmpty(vehicles)) {
        this.solrClient.deleteById(
            vehicles.stream().map(item -> this.constructVehicleSolrId(item.getId(), item.getStoreId(), item.getVehicleVersion()))
                .collect(Collectors.toList()));
      }
    } catch (final Exception e) {
      this.logger.error("Failed to delete documents in Solr", e);
    }
  }

  private String constructVehicleSolrId(final VehicleIdDO vehicleId, final String storeId, final int vehicleVersion) {
    final StringJoiner joiner =
        new StringJoiner(ID_SEPARATOR).add(vehicleId.getType().name()).add(vehicleId.getId()).add(String.valueOf(vehicleVersion));
    return new StringBuilder().append(storeId).append(ROUTE_ID_SEPARATOR).append(joiner.toString()).toString();
  }
}
