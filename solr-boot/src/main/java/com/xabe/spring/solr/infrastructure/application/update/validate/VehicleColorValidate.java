package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.entity.ColorTypeDO;
import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.update.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.TextDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class VehicleColorValidate extends ValidateVehicle<UpdateVehicleColorDTO> {

  private static final String UPDATE_VEHICLE_COLOR = "UpdateVehicleColor";

  private static final String COLORS = "colors";

  private static final String ID = "id color";

  private static final String TYPE = "type";

  private static final String COLORS_NAMES = "colors names";

  private static final String TEXT = "text";

  private static final String LOCALE = "locale";

  public VehicleColorValidate() {
    super(UPDATE_VEHICLE_COLOR);
  }

  @Override
  public void validateUpdate(final UpdateVehicleColorDTO dto) {
    if (CollectionUtils.isEmpty(dto.getColors())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_COLOR, COLORS));
    }
    dto.getColors().forEach(this::validateColor);
  }

  private void validateColor(final ColorDTO color) {
    this.validateString(color.getId(), UPDATE_VEHICLE_COLOR, ID);
    this.validateObject(ColorTypeDO.getType(color.getType()), UPDATE_VEHICLE_COLOR, TYPE);
    if (CollectionUtils.isEmpty(color.getColorNames())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_COLOR, COLORS_NAMES));
    }
    color.getColorNames().forEach(this::validateColorNames);
  }

  private void validateColorNames(final TextDTO textDTO) {
    this.validateString(textDTO.getText(), UPDATE_VEHICLE_COLOR, TEXT);
    this.validateString(textDTO.getLocale(), UPDATE_VEHICLE_COLOR, LOCALE);
  }
}
