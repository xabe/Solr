package com.xabe.spring.solr.infrastructure.controller.mapper;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.CarDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.DefinitionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.ParamsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.PricesDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.SectionsDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.TruckDTO;
import com.xabe.spring.solr.infrastructure.application.search.dto.VehicleDTO;
import com.xabe.vehicle.api.grpc.CarOuterClass.Car;
import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.PricesOuterClass.Prices;
import com.xabe.vehicle.api.grpc.SectionOuterClass.Section;
import com.xabe.vehicle.api.grpc.SectionOuterClass.Section.VehicleElement;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckOuterClass.Truck;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleRequestOuterClass.GetVehicleRequest;
import com.xabe.vehicle.api.grpc.search.getvehicle.GetVehicleResponseOuterClass.GetVehicleResponse;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ValueMapping;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface GetVehicleGrpcMapper {

  String PREFIX_COLOR = "COLOR_";
  String PREFIX_ENGINE = "ENGINE_";
  String PREFIX_FUEL = "FUEL_";
  String PREFIX_TRANSMISSION = "TRANSMISSION_";
  String PREFIX_WHEEL = "WHEEL_";
  String PREFIX_CAR_TYPE = "CAR_";
  String PREFIX_TRUCK_TYPE = "TRUCK_";
  Map<String, ColorType> COLOR_TYPE_MAP =
      Stream.of(ColorType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(ColorType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, EngineType> ENGINE_TYPE_MAP =
      Stream.of(EngineType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(EngineType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, FuelType> FUEL_TYPE_MAP =
      Stream.of(FuelType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(FuelType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, TransmissionType> TRANSMISSION_TYPE_MAP =
      Stream.of(TransmissionType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(TransmissionType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, WheelType> WHEEL_TYPE_MAP =
      Stream.of(WheelType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(WheelType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, CarType> CAR_TYPE_MAP =
      Stream.of(CarType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(CarType::name, Function.identity()), Collections::unmodifiableMap));
  Map<String, TruckType> TRUCK_TYPE_MAP =
      Stream.of(TruckType.values())
          .collect(
              Collectors.collectingAndThen(Collectors.toMap(TruckType::name, Function.identity()), Collections::unmodifiableMap));

  @Mapping(source = "sectionsList", target = "sectionDefinitions")
  DefinitionDTO mapToDefinitionDTO(GetVehicleRequest request);

  @Mapping(source = "filterHasPrices", target = "filterHasPrice")
  ParamsDTO mapToParamsDTO(GetVehicleRequest request);

  @Mapping(source = "sections", target = "sectionsList")
  GetVehicleResponse mapGetVehicleResponse(SectionsDTO sections);

  @Mapping(source = "elements", target = "elementsList")
  Section mapSectionResponse(SectionDTO section);

  default VehicleElement mapVehicleElement(final VehicleDTO vehicle) {
    if (vehicle.isCar()) {
      return VehicleElement.newBuilder().setCar(this.mapCarResponse(CarDTO.class.cast(vehicle))).build();
    } else {
      return VehicleElement.newBuilder().setTruck(this.mapTruckResponse(TruckDTO.class.cast(vehicle))).build();
    }
  }

  @Mapping(source = "category", target = "categoryList")
  @Mapping(source = "colors", target = "colorsList")
  @Mapping(source = "engines", target = "enginesList")
  @Mapping(source = "transmissions", target = "transmissionsList")
  @Mapping(source = "wheels", target = "wheelsList")
  Car mapCarResponse(CarDTO carDTO);

  @Mapping(source = "category", target = "categoryList")
  @Mapping(source = "colors", target = "colorsList")
  @Mapping(source = "engines", target = "enginesList")
  @Mapping(source = "transmissions", target = "transmissionsList")
  @Mapping(source = "wheels", target = "wheelsList")
  Truck mapTruckResponse(TruckDTO truckDTO);

  @Mapping(source = "prices", target = "pricesList")
  Prices mapColorResponse(PricesDTO prices);

  @ValueMapping(source = "UNSPECIFIED", target = "VEHICLE_UNSPECIFIED")
  @ValueMapping(source = "CAR", target = "VEHICLE_CAR")
  @ValueMapping(source = "TRUCK", target = "VEHICLE_TRUCK")
  VehicleType mapVehicleType(final VehicleTypeDTO vehicleType);

  default ColorType mapColorType(final String colorType) {
    return COLOR_TYPE_MAP.getOrDefault(PREFIX_COLOR + colorType, ColorType.UNRECOGNIZED);
  }

  default FuelType mapFuelType(final String fuelType) {
    return FUEL_TYPE_MAP.getOrDefault(PREFIX_FUEL + fuelType, FuelType.UNRECOGNIZED);
  }

  default EngineType mapEngineType(final String engineType) {
    return ENGINE_TYPE_MAP.getOrDefault(PREFIX_ENGINE + engineType, EngineType.UNRECOGNIZED);
  }

  default TransmissionType mapTransmissionType(final String transmissionType) {
    return TRANSMISSION_TYPE_MAP.getOrDefault(PREFIX_TRANSMISSION + transmissionType, TransmissionType.UNRECOGNIZED);
  }

  default WheelType mapWheelType(final String wheelType) {
    return WHEEL_TYPE_MAP.getOrDefault(PREFIX_WHEEL + wheelType, WheelType.UNRECOGNIZED);
  }

  default CarType mapCarType(final String carType) {
    return CAR_TYPE_MAP.getOrDefault(PREFIX_CAR_TYPE + carType, CarType.UNRECOGNIZED);
  }

  default TruckType mapTruckype(final String truckType) {
    return TRUCK_TYPE_MAP.getOrDefault(PREFIX_TRUCK_TYPE + truckType, TruckType.UNRECOGNIZED);
  }
}
