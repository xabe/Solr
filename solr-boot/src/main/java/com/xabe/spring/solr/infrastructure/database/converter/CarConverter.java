package com.xabe.spring.solr.infrastructure.database.converter;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.CAR_BODY_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.DOORS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VISIBILITY_VERSION;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

@Component
public class CarConverter extends AbstractVehicleConverter<CarDO> {

  public CarConverter() {
    super(VehicleTypeDO.CAR);
  }

  @Override
  public CarDO readerFromSolr(final SolrDocument solrDocument) {
    final CarDO carDO = new CarDO(this.createVehicleIdDO(solrDocument), this.getStoreId(solrDocument),
        this.getFieldValue(solrDocument, VEHICLE_VERSION), this.getFieldValue(solrDocument, VISIBILITY_VERSION));
    this.readFromSolr(solrDocument, carDO);
    carDO.setDoors(this.getFieldValue(solrDocument, DOORS));
    carDO.setType(CarTypeDO.getType(this.<String>getFieldValue(solrDocument, CAR_BODY_TYPE)));
    return carDO;
  }

  @Override
  public SolrInputDocument writeToSolr(final CarDO vehicle) {
    final SolrInputDocument solrInputDocument = new SolrInputDocument();
    this.writeToSolr(solrInputDocument, vehicle);
    solrInputDocument.setField(DOORS, vehicle.getDoors());
    vehicle.getType().ifPresent(item -> solrInputDocument.setField(CAR_BODY_TYPE, item.name()));
    return solrInputDocument;
  }

}
