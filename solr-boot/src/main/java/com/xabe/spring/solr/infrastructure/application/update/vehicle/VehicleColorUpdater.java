package com.xabe.spring.solr.infrastructure.application.update.vehicle;

import com.xabe.spring.solr.domain.entity.ColorDO;
import com.xabe.spring.solr.domain.entity.ColorTypeDO;
import com.xabe.spring.solr.domain.entity.TextDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.infrastructure.application.update.dto.ColorDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.TextDTO;
import com.xabe.spring.solr.infrastructure.application.update.dto.UpdateVehicleColorDTO;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleColorUpdater extends VehicleUpdater<VehicleDO> {

  public static final String UPDATER_ID = "vehicleColor";

  private final UpdateVehicleColorDTO updateVehicleColor;

  public VehicleColorUpdater(final UpdateVehicleColorDTO updateVehicleColor) {
    super(UpdaterType.PREVIEW, updateVehicleColor.getTimestamp(), UPDATER_ID, Boolean.FALSE);
    this.updateVehicleColor = updateVehicleColor;
  }

  @Override
  public VehicleDO update(final VehicleDO vehicle) {
    vehicle.setColors(this.updateVehicleColor.getColors().stream().map(this::mapColor).collect(Collectors.toList()));
    return vehicle;
  }

  private ColorDO mapColor(final ColorDTO color) {
    return ColorDO.builder().id(color.getId()).type(ColorTypeDO.getType(color.getType())).names(this.mapText(color.getColorNames()))
        .build();
  }

  private List<TextDO> mapText(final List<TextDTO> colorNames) {
    return colorNames.stream().map(item -> TextDO.builder().text(item.getText()).locale(item.getLocale()).build())
        .collect(Collectors.toList());
  }

}
