package com.xabe.spring.solr.infrastructure.database.converter;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.BRAND;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.CATEGORY;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_NAME;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.ENGINES;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.ENGINES_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.FUELS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.GROUP_ID_FIELD;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.MODEL;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.PRICES;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.PRICES_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.STORE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.TAGS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.TRANSMISSIONS_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.UPDATE_INSTANT;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.UPDATE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_HAS_PRICES;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VISIBILITY_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_SIZE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_TYPE;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xabe.spring.solr.domain.entity.ColorDO;
import com.xabe.spring.solr.domain.entity.EngineDO;
import com.xabe.spring.solr.domain.entity.PriceDO;
import com.xabe.spring.solr.domain.entity.PricesDO;
import com.xabe.spring.solr.domain.entity.TextDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleDO;
import com.xabe.spring.solr.domain.entity.VehicleTypeDO;
import com.xabe.spring.solr.domain.entity.WheelDO;
import com.xabe.spring.solr.domain.entity.WheelSizeDO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public abstract class AbstractVehicleConverter<T extends VehicleDO> implements VehicleConverter<T> {

  private final Gson gson;

  private final TypeToken<List<EngineDO>> engines;

  private final TypeToken<PricesDO> prices;

  private final TypeToken<List<WheelDO>> wheels;

  private final TypeToken<List<TransmissionTypeDO>> transmissions;

  private final TypeToken<List<ColorDO>> colors;

  private final VehicleTypeDO vehicleTypeDO;

  public AbstractVehicleConverter(final VehicleTypeDO vehicleTypeDO) {
    this.vehicleTypeDO = vehicleTypeDO;
    this.gson = new Gson();
    this.engines = new TypeToken<List<EngineDO>>() {
    };
    this.prices = new TypeToken<PricesDO>() {
    };
    this.wheels = new TypeToken<List<WheelDO>>() {
    };
    this.transmissions = new TypeToken<List<TransmissionTypeDO>>() {
    };
    this.colors = new TypeToken<List<ColorDO>>() {
    };
  }

  @Override
  public VehicleTypeDO getType() {
    return this.vehicleTypeDO;
  }

  protected void readFromSolr(final SolrDocument solrDocument, final T vehicle) {
    vehicle.setUpdateInstants(this.getInstantMapFromSolr(solrDocument));
    vehicle.setUpdateVersion(this.getFieldValue(solrDocument, UPDATE_VERSION));
    vehicle.setBrand(this.getFieldValue(solrDocument, BRAND));
    vehicle.setModel(this.getFieldValue(solrDocument, MODEL));
    vehicle.setCategory(this.getFieldValue(solrDocument, CATEGORY));
    vehicle.setTags(this.getNullableCollectionSet(solrDocument, TAGS));
    this.<List<EngineDO>>readFromSolr(solrDocument, ENGINES_BLOB, this.engines).ifPresent(vehicle::setEngines);
    this.<PricesDO>readFromSolr(solrDocument, PRICES_BLOB, this.prices).ifPresent(vehicle::setPrices);
    this.<List<WheelDO>>readFromSolr(solrDocument, WHEELS_BLOB, this.wheels).ifPresent(vehicle::setWheels);
    vehicle.setTransmissions(
        this.<String>getNullableCollectionList(solrDocument, TRANSMISSIONS_TYPE).stream().map(TransmissionTypeDO::getType)
            .collect(Collectors.toList()));
    this.<List<ColorDO>>readFromSolr(solrDocument, COLORS_BLOB, this.colors).ifPresent(vehicle::setColors);
  }

  protected <T> Set<T> getNullableCollectionSet(final SolrDocument solrDocument, final String key) {
    final Collection values = solrDocument.getFieldValues(key);
    return values != null ? new HashSet(values) : Collections.emptySet();
  }

  protected <T> List<T> getNullableCollectionList(final SolrDocument solrDocument, final String key) {
    final Collection values = solrDocument.getFieldValues(key);
    return values != null ? new ArrayList<>(values) : Collections.emptyList();
  }

  protected <T> Optional<T> readFromSolr(final SolrDocument solrDocument, final String key, final TypeToken<T> typeToken) {
    final String value = (String) solrDocument.getFieldValue(key);
    if (StringUtils.isBlank(value)) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.gson.fromJson(value, typeToken.getType()));
  }

  protected void writeToSolr(final SolrInputDocument solrInputDocument, final T vehicle) {
    solrInputDocument.setField(ID, this.getIdToString(vehicle));
    solrInputDocument.setField(GROUP_ID_FIELD, this.getGroupIdToString(vehicle));
    solrInputDocument.setField(STORE_ID, vehicle.getStoreId());
    solrInputDocument.setField(VEHICLE_ID, vehicle.getId().getId());
    solrInputDocument.setField(VEHICLE_TYPE, vehicle.getId().getType().name());
    solrInputDocument.setField(VEHICLE_VERSION, vehicle.getVehicleVersion());
    solrInputDocument.setField(UPDATE_VERSION, vehicle.getUpdateVersion());
    solrInputDocument.setField(VISIBILITY_VERSION, vehicle.getVisibilityVersionTimestamp());
    vehicle.getUpdateInstants().forEach((key, value) -> solrInputDocument.setField(UPDATE_INSTANT + key, value.toEpochMilli()));
    solrInputDocument.setField(BRAND, vehicle.getBrand());
    solrInputDocument.setField(MODEL, vehicle.getModel());
    solrInputDocument.setField(CATEGORY, vehicle.getCategory());
    solrInputDocument.setField(TAGS, vehicle.getTags());
    solrInputDocument.setField(ENGINES, vehicle.getEngines().stream().map(EngineDO::getType).map(Enum::name).collect(Collectors.toSet()));
    solrInputDocument.setField(FUELS, vehicle.getEngines().stream().map(EngineDO::getFuel).map(Enum::name).collect(Collectors.toSet()));
    solrInputDocument.setField(ENGINES_BLOB, this.writeToSolr(vehicle.getEngines()));
    solrInputDocument.setField(VEHICLE_HAS_PRICES, vehicle.hasPrices());
    vehicle.getPrices().ifPresent(pricesDO -> this.writePrices(pricesDO, solrInputDocument));
    solrInputDocument
        .setField(WHEELS_SIZE, vehicle.getWheels().stream().map(WheelDO::getSize).map(WheelSizeDO::getValue).collect(Collectors.toSet()));
    solrInputDocument.setField(WHEELS_TYPE, vehicle.getWheels().stream().map(WheelDO::getType).map(Enum::name).collect(Collectors.toSet()));
    solrInputDocument.setField(WHEELS_BLOB, this.writeToSolr(vehicle.getWheels()));
    solrInputDocument.setField(TRANSMISSIONS_TYPE, vehicle.getTransmissions().stream().map(Enum::name).collect(Collectors.toSet()));
    solrInputDocument.setField(COLORS_TYPE, vehicle.getColors().stream().map(ColorDO::getType).map(Enum::name).collect(Collectors.toSet()));
    final Map<String, List<TextDO>> mapColorName = vehicle.getColors().stream().flatMap(item -> item.getNames().stream())
        .collect(Collectors.groupingBy(TextDO::getLocale));
    mapColorName.forEach((key, value) ->
        solrInputDocument.setField(COLORS_NAME + key, value.stream().map(TextDO::getText).collect(Collectors.toSet())));
    solrInputDocument.setField(COLORS_BLOB, this.writeToSolr(vehicle.getColors()));
  }

  protected String writeToSolr(final Object element) {
    if (Objects.isNull(element)) {
      return null;
    }
    return this.gson.toJson(element);
  }

  private void writePrices(final PricesDO prices, final SolrInputDocument solrInputDocument) {
    if (CollectionUtils.isNotEmpty(prices.getPrices())) {
      solrInputDocument.setField(PRICES, prices.getPrices().stream().map(PriceDO::getPrice).collect(Collectors.toSet()));
    }
    solrInputDocument.setField(PRICES_BLOB, this.writeToSolr(prices));
  }
}
