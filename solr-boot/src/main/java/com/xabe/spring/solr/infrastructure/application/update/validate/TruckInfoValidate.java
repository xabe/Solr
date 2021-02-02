package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class TruckInfoValidate extends ValidateVehicle<UpdateTruckInfoDTO> {

  private static final String UPDATE_TRUCK_INFO = "UpdateTruckInfo";

  private static final String TYPE = "truck type";

  public TruckInfoValidate() {
    super(UPDATE_TRUCK_INFO);
  }

  @Override
  public void validateUpdate(final UpdateTruckInfoDTO dto) {
    this.validateObject(TruckTypeDO.getType(dto.getType()), UPDATE_TRUCK_INFO, TYPE);
  }
}
