package com.xabe.spring.solr.infrastructure.database.converter;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.STORE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.UPDATE_INSTANT;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ID_SEPARATOR;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ROUTE_ID_SEPARATOR;

import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.infrastructure.application.util.TimeUtil;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public interface VehicleConverter<T extends VehicleDO> {

  VehicleTypeDO getType();

  SolrInputDocument writeToSolr(T vehicle);

  T readerFromSolr(SolrDocument solrDocument);

  default VehicleIdDO createVehicleIdDO(final SolrDocument solrDocument) {
    final String id = this.getFieldValue(solrDocument, VEHICLE_ID);
    final VehicleTypeDO vehicleTypeDO = VehicleTypeDO.getType(this.<String>getFieldValue(solrDocument, VEHICLE_TYPE));
    return VehicleIdDO.builder().id(id).type(vehicleTypeDO).build();
  }

  default Map<String, Instant> getInstantMapFromSolr(final SolrDocument solrDocument) {
    final Map<String, Instant> instants = new HashMap<>();

    solrDocument.getFieldNames().stream().filter(name -> name.startsWith(UPDATE_INSTANT)).forEach(fieldName -> {
      final Long epochMilli = this.getFieldValue(solrDocument, fieldName);
      final String key = StringUtils.removeStart(fieldName, UPDATE_INSTANT);
      instants.put(key, Instant.ofEpochMilli(TimeUtil.getMillis(epochMilli)));
    });

    return instants;
  }

  default String getStoreId(final SolrDocument document) {
    return this.getFieldValue(document, STORE_ID);
  }

  default <T> T getFieldValue(final SolrDocument doc, final String key) {
    return (T) doc.getFieldValue(key);
  }

  default String getIdToString(final T element) {
    final VehicleIdDO id = element.getId();
    final StringJoiner joiner =
        new StringJoiner(ID_SEPARATOR).add(id.getType().name()).add(id.getId()).add(String.valueOf(element.getVehicleVersion()));
    return element.getStoreId() + ROUTE_ID_SEPARATOR + joiner.toString();
  }

  default String getGroupIdToString(final T element) {
    return element.getId().getType().name() + ID_SEPARATOR + element.getId().getId();
  }
}
