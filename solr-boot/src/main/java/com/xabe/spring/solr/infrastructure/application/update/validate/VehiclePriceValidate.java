package com.xabe.spring.solr.infrastructure.application.update.validate;

import com.xabe.spring.solr.domain.exception.InvalidUpdateVehicleException;
import com.xabe.spring.solr.infrastructure.application.update.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class VehiclePriceValidate extends ValidateVehicle<UpdateVehiclePriceDTO> {

  private static final String UPDATE_VEHICLE_ENGINE = "UpdateVehiclePrice";

  private static final String PRICES = "prices";

  private static final String PRICE = "price";

  private static final String CURRENCY_CODE = "currency code";

  public VehiclePriceValidate() {
    super(UPDATE_VEHICLE_ENGINE);
  }

  @Override
  public void validateUpdate(final UpdateVehiclePriceDTO dto) {
    this.validateString(dto.getPrices().getCurrencyCode(), UPDATE_VEHICLE_ENGINE, CURRENCY_CODE);
    if (CollectionUtils.isEmpty(dto.getPrices().getPrices())) {
      throw new InvalidUpdateVehicleException(String.format(CONTAINS_AN_INVALID_VALUE, UPDATE_VEHICLE_ENGINE, PRICES));
    }
    dto.getPrices().getPrices().forEach(this::validatePrice);
  }

  private void validatePrice(final PriceDTO price) {
    this.validateLong(price.getPrice(), UPDATE_VEHICLE_ENGINE, PRICE);
  }
}
