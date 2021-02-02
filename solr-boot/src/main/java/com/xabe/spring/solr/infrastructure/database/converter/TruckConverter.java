package com.xabe.spring.solr.infrastructure.database.converter;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.TRUCK_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VISIBILITY_VERSION;

import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

@Component
public class TruckConverter extends AbstractVehicleConverter<TruckDO> {

  public TruckConverter() {
    super(VehicleTypeDO.TRUCK);
  }

  @Override
  public SolrInputDocument writeToSolr(final TruckDO vehicle) {
    final SolrInputDocument solrInputDocument = new SolrInputDocument();
    this.writeToSolr(solrInputDocument, vehicle);
    vehicle.getType().ifPresent(item -> solrInputDocument.setField(TRUCK_TYPE, item.name()));
    return solrInputDocument;
  }

  @Override
  public TruckDO readerFromSolr(final SolrDocument solrDocument) {
    final TruckDO truckDO = new TruckDO(this.createVehicleIdDO(solrDocument), this.getStoreId(solrDocument),
        this.getFieldValue(solrDocument, VEHICLE_VERSION), this.getFieldValue(solrDocument, VISIBILITY_VERSION));
    this.readFromSolr(solrDocument, truckDO);
    truckDO.setType(TruckTypeDO.getType(this.<String>getFieldValue(solrDocument, TRUCK_TYPE)));
    return truckDO;
  }
}
