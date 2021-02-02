package com.xabe.spring.solr.infrastructure.application.search.service.mapper;

import com.xabe.spring.solr.domain.entity.CarDO;
import com.xabe.spring.solr.domain.entity.CarTypeDO;
import com.xabe.spring.solr.domain.entity.ColorDO;
import com.xabe.spring.solr.domain.entity.EngineDO;
import com.xabe.spring.solr.domain.entity.PriceDO;
import com.xabe.spring.solr.domain.entity.PricesDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.WheelDO;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO;
import com.xabe.spring.solr.infrastructure.application.dto.EngineDTO.EngineDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleIdDTO;
import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.dto.WheelDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.CarDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ColorDTO.ColorDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.search.dto.PriceDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.PriceDTO.PriceDTOBuilder;
import com.xabe.spring.solr.infrastructure.application.search.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.TruckDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.VehicleDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.money.Monetary;
import javax.money.format.AmountFormatQuery;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import org.apache.commons.collections.CollectionUtils;
import org.javamoney.moneta.format.CurrencyStyle;
import org.springframework.stereotype.Component;

@Component
public class VehiclesMapper {

  private final Map<Class, VehicleMapper> vehicleMapper;

  public VehiclesMapper() {
    this.vehicleMapper = new HashMap();
    final VehicleMapper<CarDO> mapperCar = this::mapperCar;
    final VehicleMapper<TruckDO> mapperTruck = this::mapperTruck;
    this.vehicleMapper.put(CarDO.class, mapperCar);
    this.vehicleMapper.put(TruckDO.class, mapperTruck);
  }

  public VehicleDTO mapper(final VehicleDO vehicle, final MappingContext mappingContext) {
    return this.vehicleMapper.get(vehicle.getClass()).mapper(vehicle, mappingContext);
  }

  private VehicleDTO mapperCar(final CarDO car, final MappingContext mappingContext) {
    final CarDTO.CarDTOBuilder builder = CarDTO.builder();
    this.mapperVehicle(car, builder, mappingContext);
    builder.doors(car.getDoors());
    car.getType().map(CarTypeDO::name).ifPresent(builder::type);
    return builder.build();
  }

  private VehicleDTO mapperTruck(final TruckDO truck, final MappingContext mappingContext) {
    final TruckDTO.TruckDTOBuilder builder = TruckDTO.builder();
    this.mapperVehicle(truck, builder, mappingContext);
    truck.getType().map(TruckTypeDO::name).ifPresent(builder::type);
    return builder.build();
  }

  private void mapperVehicle(final VehicleDO vehicle, final VehicleDTO.VehicleDTOBuilder builder, final MappingContext mappingContext) {
    builder.vehicleId(this.mapId(vehicle.getId()));
    builder.category(new ArrayList<>(vehicle.getTags()));
    builder.brand(vehicle.getBrand());
    builder.model(vehicle.getModel());
    builder.colors(this.mapColors(vehicle.getColors(), mappingContext));
    builder.engines(this.mapEngines(vehicle.getEngines()));
    vehicle.getPrices().map(this.mapPrices(mappingContext)).ifPresent(builder::prices);
    if (CollectionUtils.isNotEmpty(vehicle.getTransmissions())) {
      builder.transmissions(vehicle.getTransmissions().stream().map(TransmissionTypeDO::name).collect(Collectors.toList()));
    }
    builder.wheels(this.mapWheels(vehicle.getWheels()));
  }

  private VehicleIdDTO mapId(final VehicleIdDO id) {
    return VehicleIdDTO.builder().id(id.getId()).type(VehicleTypeDTO.getVehicleType(id.getType().name())).build();
  }

  private List<ColorDTO> mapColors(final List<ColorDO> colors, final MappingContext mappingContext) {
    if (CollectionUtils.isEmpty(colors)) {
      return List.of();
    }
    return colors.stream().map(this.mapColor(mappingContext)).collect(Collectors.toList());
  }

  private Function<ColorDO, ColorDTO> mapColor(final MappingContext mappingContext) {
    return color -> {
      final ColorDTOBuilder builder = ColorDTO.builder();
      builder.id(color.getId());
      builder.type(color.getType().name());
      builder.name(color.getName(mappingContext));
      return builder.build();
    };
  }

  private List<EngineDTO> mapEngines(final List<EngineDO> engines) {
    if (CollectionUtils.isEmpty(engines)) {
      return List.of();
    }
    return engines.stream().map(this::mapEngine).collect(Collectors.toList());
  }

  private EngineDTO mapEngine(final EngineDO engine) {
    final EngineDTOBuilder builder = EngineDTO.builder();
    builder.fuel(engine.getFuel().name());
    builder.type(engine.getType().name());
    return builder.build();
  }

  private Function<PricesDO, PricesDTO> mapPrices(final MappingContext mappingContext) {
    return prices -> {
      final AmountFormatQuery pattern =
          AmountFormatQueryBuilder.of(new Locale(mappingContext.getLocale())).set(CurrencyStyle.SYMBOL).build();
      final MonetaryAmountFormat monetaryAmountFormat = MonetaryFormats.getAmountFormat(pattern);
      return PricesDTO.builder().currencyCode(prices.getCurrencyCode())
          .prices(
              prices.getPrices().stream().map(this.mapPrice(prices.getCurrencyCode(), monetaryAmountFormat)).collect(Collectors.toList()))
          .build();
    };
  }

  private Function<PriceDO, PriceDTO> mapPrice(final String currencyCode, final MonetaryAmountFormat monetaryAmountFormat) {
    return price -> {
      final PriceDTOBuilder builder = PriceDTO.builder();
      builder.price(price.getPrice());
      builder.discount(price.getDiscount());
      builder.priceFormat(
          monetaryAmountFormat.format(Monetary.getDefaultAmountFactory().setCurrency(currencyCode).setNumber(price.getPrice()).create()));
      return builder.build();
    };
  }

  private List<WheelDTO> mapWheels(final List<WheelDO> wheels) {
    if (CollectionUtils.isEmpty(wheels)) {
      return List.of();
    }
    return wheels.stream().map(this::mapWheel).collect(Collectors.toList());
  }

  private WheelDTO mapWheel(final WheelDO wheel) {
    return WheelDTO.builder().size(wheel.getSize().getValue()).count(wheel.getCount()).type(wheel.getType().name()).build();
  }
}
