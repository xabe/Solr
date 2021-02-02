package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.PriceDO;
import com.xabe.spring.solr.domain.entity.PricesDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import java.util.List;
import java.util.stream.Collectors;

public class VehiclePriceUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehiclePrice";

  private final UpdateVehiclePriceDTO updateVehiclePrice;

  public VehiclePriceUpdater(final UpdateVehiclePriceDTO updateVehiclePrice) {
    super(UpdaterType.PREVIEW, updateVehiclePrice.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.updateVehiclePrice = updateVehiclePrice;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    final PricesDTO prices = this.updateVehiclePrice.getPrices();
    final List<PriceDO> result = prices.getPrices().stream().map(this::mapPrice).collect(Collectors.toList());
    vehicle.setPrices(PricesDO.builder().prices(result).currencyCode(prices.getCurrencyCode()).build());
    return vehicle;
  }

  private PriceDO mapPrice(final PriceDTO price) {
    return PriceDO.builder().price(price.getPrice()).discount(price.getDiscount()).build();
  }
}
