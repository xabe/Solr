package com.xabe.spring.solr.infrastructure.controller.mapper;

import com.xabe.spring.solr.infrastructure.application.dto.VehicleTypeDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateCarInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateTruckInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleBasicInfoDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleEngineDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehiclePriceDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleTransmissionDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleWheelDTO;
import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.TruckTypeOuterClass.TruckType;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoRequestOuterClass.UpdateCarInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateTruckInfoRequestOuterClass.UpdateTruckInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.Color;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ValueMapping;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface UpdateVehicleGrpcMapper {

  String PREFIX_WHEEL = "WHEEL_";
  String PREFIX_ENGINE = "ENGINE_";
  String PREFIX_FUEL = "FUEL_";
  String PREFIX_COLOR = "COLOR_";
  String PREFIX_TRANSMISSION = "TRANSMISSION_";
  String PREFIX_CAR = "CAR_";
  String PREFIX_TRUCK = "TRUCK_";

  UpdateVehicleBasicInfoDTO requestToDTO(UpdateVehicleBasicRequest request);

  @Mapping(source = "enginesList", target = "engines")
  UpdateVehicleEngineDTO requestToDTO(UpdateVehicleEngineRequest request);

  @Mapping(source = "prices.pricesList", target = "prices.prices")
  UpdateVehiclePriceDTO requestToDTO(UpdateVehiclePriceRequest request);

  @Mapping(source = "wheelsList", target = "wheels")
  UpdateVehicleWheelDTO requestToDTO(UpdateVehicleWheelRequest request);

  @Mapping(source = "colorsList", target = "colors")
  UpdateVehicleColorDTO requestToDTO(UpdateVehicleColorRequest request);

  @Mapping(source = "colorNamesList", target = "colorNames")
  ColorDTO requestToDTO(Color color);

  @Mapping(source = "transmissionsList", target = "transmissions")
  UpdateVehicleTransmissionDTO requestToDTO(UpdateVehicleTransmissionRequest request);

  UpdateCarInfoDTO requestToDTO(UpdateCarInfoRequest request);

  UpdateTruckInfoDTO requestToDTO(UpdateTruckInfoRequest request);

  @ValueMapping(source = "UNRECOGNIZED", target = "<NULL>")
  @ValueMapping(source = "VEHICLE_UNSPECIFIED", target = "UNSPECIFIED")
  @ValueMapping(source = "VEHICLE_CAR", target = "CAR")
  @ValueMapping(source = "VEHICLE_TRUCK", target = "TRUCK")
  VehicleTypeDTO vehicleTypeDTO(final VehicleType vehicleType);

  default String wheelType(final WheelType wheelType) {
    return Objects.isNull(wheelType) ? StringUtils.EMPTY : StringUtils.removeStart(wheelType.name(), PREFIX_WHEEL);
  }

  default String engineType(final EngineType engineType) {
    return Objects.isNull(engineType) ? StringUtils.EMPTY : StringUtils.removeStart(engineType.name(), PREFIX_ENGINE);
  }

  default String fuelType(final FuelType fuelType) {
    return Objects.isNull(fuelType) ? StringUtils.EMPTY : StringUtils.removeStart(fuelType.name(), PREFIX_FUEL);
  }

  default String fuelType(final ColorType colorType) {
    return Objects.isNull(colorType) ? StringUtils.EMPTY : StringUtils.removeStart(colorType.name(), PREFIX_COLOR);
  }

  default String transmissionType(final TransmissionType transmissionType) {
    return Objects.isNull(transmissionType) ? StringUtils.EMPTY : StringUtils.removeStart(transmissionType.name(), PREFIX_TRANSMISSION);
  }

  default String carType(final CarType carType) {
    return Objects.isNull(carType) ? StringUtils.EMPTY : StringUtils.removeStart(carType.name(), PREFIX_CAR);
  }

  default String truckType(final TruckType truckType) {
    return Objects.isNull(truckType) ? StringUtils.EMPTY : StringUtils.removeStart(truckType.name(), PREFIX_TRUCK);
  }

}
