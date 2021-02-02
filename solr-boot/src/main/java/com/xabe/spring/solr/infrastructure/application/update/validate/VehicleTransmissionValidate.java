package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class VehicleTransmissionValidate extends ValidateVehicle<UpdateVehicleTransmissionDTO> {

  private static final String UPDATE_VEHICLE_TRANSMISSION = "UpdateVehicleTransmission";

  private static final String TYPE = "transmission type";

  private static final String TRANSMISSION = "transmission";

  public VehicleTransmissionValidate() {
    super(UPDATE_VEHICLE_TRANSMISSION);
  }

  @Override
  public void validateUpdate(final UpdateVehicleTransmissionDTO dto) {
    if (CollectionUtils.isEmpty(dto.getTransmissions())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_TRANSMISSION, TRANSMISSION));
    }
    dto.getTransmissions().forEach(this::validateTransmission);
  }

  private void validateTransmission(final String transmission) {
    this.validateObject(TransmissionTypeDO.getType(transmission), UPDATE_VEHICLE_TRANSMISSION, TYPE);
  }
}
