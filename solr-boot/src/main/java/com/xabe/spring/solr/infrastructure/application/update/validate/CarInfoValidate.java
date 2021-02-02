package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class CarInfoValidate extends ValidateVehicle<UpdateCarInfoDTO> {

  private static final String UPDATE_CAR_INFO = "UpdateCarInfo";

  private static final String DOORS = "doors";

  private static final String CAR_TYPE = "car type";

  public CarInfoValidate() {
    super(UPDATE_CAR_INFO);
  }

  @Override
  public void validateUpdate(final UpdateCarInfoDTO dto) {
    this.validateInteger(dto.getDoors(), UPDATE_CAR_INFO, DOORS);
    this.validateObject(CarTypeDO.getType(dto.getType()), UPDATE_CAR_INFO, CAR_TYPE);
  }
}
