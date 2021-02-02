package com.xabe.spring.solr.infrastructure.application.update.validate;

import static com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO.UNSPECIFIED;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateBaseDTO;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public abstract class ValidateVehicle<T extends UpdateBaseDTO> {

  private static final String STORE_ID = "store id";

  private static final String TIMESTAMP = "timestamp";

  private static final String ID = "id";

  protected static final String CONTAINS_AN_INVALID_VALUE = "%s contains an invalid value %s";

  private final String updateId;

  public ValidateVehicle(final String updateId) {
    this.updateId = updateId;
  }

  public void validate(final T dto) {
    this.validateId(dto.getId(), this.updateId);
    this.validateString(dto.getStoreId(), this.updateId, STORE_ID);
    this.validateLong(dto.getTimestamp(), this.updateId, TIMESTAMP);
    this.validateUpdate(dto);
  }

  protected abstract void validateUpdate(T dto);

  protected void validateId(final VehicleIdDTO id, final String dto) {
    if (Objects.isNull(id) || StringUtils.isBlank(id.getId()) || Objects.isNull(id.getType()) || UNSPECIFIED.equals(id.getType())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, dto, ID));
    }
  }

  protected void validateString(final String value, final String command, final String attrName) {
    if (StringUtils.isBlank(value)) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, command, attrName));
    }
  }

  protected void validateLong(final Long value, final String command, final String attrName) {
    if (Objects.isNull(value) || value <= 0) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, command, attrName));
    }
  }

  protected void validateInteger(final Integer value, final String command, final String attrName) {
    if (Objects.isNull(value) || value <= 0) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, command, attrName));
    }
  }

  protected void validateObject(final Object value, final String command, final String attrName) {
    if (Objects.isNull(value)) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, command, attrName));
    }
  }
}
