package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.entity.WheelTypeDO;
import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class VehicleWheelValidate extends ValidateVehicle<UpdateVehicleWheelDTO> {

  private static final String UPDATE_VEHICLE_WHEEL = "UpdateVehicleWheel";

  private static final String WHEELS = "wheels";

  private static final String COUNT = "count";

  private static final String SIZE = "size";

  private static final String TYPE = "type";

  public VehicleWheelValidate() {
    super(UPDATE_VEHICLE_WHEEL);
  }

  @Override
  public void validateUpdate(final UpdateVehicleWheelDTO dto) {
    if (CollectionUtils.isEmpty(dto.getWheels())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_WHEEL, WHEELS));
    }
    dto.getWheels().forEach(this::validateWheel);
  }

  private void validateWheel(final WheelDTO wheel) {
    this.validateInteger(wheel.getCount(), UPDATE_VEHICLE_WHEEL, COUNT);
    this.validateInteger(wheel.getSize(), UPDATE_VEHICLE_WHEEL, SIZE);
    this.validateObject(WheelTypeDO.getType(wheel.getType()), UPDATE_VEHICLE_WHEEL, TYPE);
  }
}
