package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class VehicleBasicInfoValidate extends ValidateVehicle<UpdateVehicleBasicInfoDTO> {

  public static final String UPDATE_VEHICLE_BASIC_INFO = "UpdateVehicleBasicInfo";

  public static final String BRAND = "brand";

  public static final String MODEL = "model";

  public static final String CATEGORY = "category";

  public VehicleBasicInfoValidate() {
    super(UPDATE_VEHICLE_BASIC_INFO);
  }

  @Override
  public void validateUpdate(final UpdateVehicleBasicInfoDTO dto) {
    this.validateString(dto.getBrand(), UPDATE_VEHICLE_BASIC_INFO, BRAND);
    this.validateString(dto.getModel(), UPDATE_VEHICLE_BASIC_INFO, MODEL);
    this.validateString(dto.getCategory(), UPDATE_VEHICLE_BASIC_INFO, CATEGORY);
  }
}
