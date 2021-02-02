package com.xabe.spring.solr.infrastructure.database.converter;

import static com.xabe.spring.solr.domain.entity.VehicleTypeDO.TRUCK;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.BRAND;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.CATEGORY;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_NAME;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.COLORS_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.DOORS;
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
import static com.xabe.spring.solr.infrastructure.database.SolrFields.TRUCK_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.UPDATE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_HAS_PRICES;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VISIBILITY_VERSION;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_BLOB;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_SIZE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.WHEELS_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.google.gson.Gson;
import com.xabe.spring.solr.domain.entity.ColorDO;
import com.xabe.spring.solr.domain.entity.ColorTypeDO;
import com.xabe.spring.solr.domain.entity.EngineDO;
import com.xabe.spring.solr.domain.entity.EngineTypeDO;
import com.xabe.spring.solr.domain.entity.FuelTypeDO;
import com.xabe.spring.solr.domain.entity.PriceDO;
import com.xabe.spring.solr.domain.entity.PricesDO;
import com.xabe.spring.solr.domain.entity.TextDO;
import com.xabe.spring.solr.domain.entity.TransmissionTypeDO;
import com.xabe.spring.solr.domain.entity.TruckDO;
import com.xabe.spring.solr.domain.entity.TruckTypeDO;
import com.xabe.spring.solr.domain.entity.VehicleIdDO;
import com.xabe.spring.solr.domain.entity.WheelDO;
import com.xabe.spring.solr.domain.entity.WheelSizeDO;
import com.xabe.spring.solr.domain.entity.WheelTypeDO;
import com.xabe.spring.solr.infrastructure.application.util.TimeUtil;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TruckConverterTest {

  private final Instant in = Instant.ofEpochMilli(TimeUtil.getMillis(System.currentTimeMillis()));

  private Gson gson;

  private final TruckDO truckDO =
      TruckDO.builder().vehicleVersion(1).updateVersion(1L).visibilityVersionTimestamp(2L)
          .id(VehicleIdDO.builder().type(TRUCK).id("id").build()).brand("brand")
          .model("model").storeId("sp").category("category").tags(Set.of("tags", "tag"))
          .engines(List.of(EngineDO.builder().fuel(FuelTypeDO.ELECTRIC).type(EngineTypeDO.FLAT).build()))
          .prices(PricesDO.builder().currencyCode("EUR").prices(List.of(PriceDO.builder().price(100L).discount(10).build())).build())
          .wheels(List.of(WheelDO.builder().count(4).size(WheelSizeDO.R_14).type(WheelTypeDO.SUMMER).build()))
          .transmissions(List.of(TransmissionTypeDO.AUTOMATIC, TransmissionTypeDO.MANUAL))
          .colors(List.of(
              ColorDO.builder().id("1").type(ColorTypeDO.METALLIC)
                  .names(
                      List.of(TextDO.builder().text("rojo").locale("es-ES").build(), TextDO.builder().text("red").locale("en-GB").build()))
                  .build(),
              ColorDO.builder().id("2").type(ColorTypeDO.SOLID)
                  .names(List.of(TextDO.builder().text("amarillo").locale("es-ES").build(),
                      TextDO.builder().text("yellow").locale("en-GB").build())).build()))
          .type(TruckTypeDO.CEMENT)
          .updateInstants(Map.of("basic", this.in)).build();

  private VehicleConverter<TruckDO> vehicleConverter;

  @BeforeEach
  public void setUp() throws Exception {
    this.gson = new Gson();
    this.vehicleConverter = new TruckConverter();
  }

  @Test
  public void getType() throws Exception {
    assertThat(this.vehicleConverter.getType(), is(TRUCK));
  }

  @Test
  public void givenASolrDocumentWhenInvokeReaderFromSolrThenReturnTruckDO() throws Exception {
    //Given
    final SolrDocument solrDocument = new SolrDocument();
    solrDocument.setField(VEHICLE_ID, "id");
    solrDocument.setField(VEHICLE_TYPE, "TRUCK");
    solrDocument.setField(STORE_ID, "sp");
    solrDocument.setField(VEHICLE_VERSION, 1);
    solrDocument.setField(VISIBILITY_VERSION, 2L);
    solrDocument.setField("updateInstant_basic", this.in.toEpochMilli());
    solrDocument.setField(UPDATE_VERSION, 1L);
    solrDocument.setField(BRAND, "brand");
    solrDocument.setField(MODEL, "model");
    solrDocument.setField(CATEGORY, "category");
    solrDocument.setField(TAGS, Set.of("tags", "tag"));
    solrDocument.setField(ENGINES_BLOB, this.gson.toJson(this.truckDO.getEngines()));
    solrDocument.setField(PRICES_BLOB, this.gson.toJson(this.truckDO.getPrices().get()));
    solrDocument.setField(WHEELS_BLOB, this.gson.toJson(this.truckDO.getWheels()));
    solrDocument.setField(TRANSMISSIONS_TYPE, List.of("AUTOMATIC", "MANUAL"));
    solrDocument.setField(COLORS_BLOB, this.gson.toJson(this.truckDO.getColors()));
    solrDocument.setField(DOORS, 4);
    solrDocument.setField(TRUCK_TYPE, "CEMENT");

    //When
    final TruckDO result = this.vehicleConverter.readerFromSolr(solrDocument);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result, is(this.truckDO));
  }

  @Test
  public void givenATruckDOWhenInvokeWriteToSolrThenReturnSolrDocumentInput() throws Exception {
    //Given

    //When
    final SolrInputDocument result = this.vehicleConverter.writeToSolr(this.truckDO);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.getFieldValue(ID), is("sp!TRUCK::id::1"));
    assertThat(result.getFieldValue(GROUP_ID_FIELD), is("TRUCK::id"));
    assertThat(result.getFieldValue(STORE_ID), is("sp"));
    assertThat(result.getFieldValue(VEHICLE_ID), is("id"));
    assertThat(result.getFieldValue(VEHICLE_TYPE), is("TRUCK"));
    assertThat(result.getFieldValue(VEHICLE_VERSION), is(1));
    assertThat(result.getFieldValue(UPDATE_VERSION), is(1L));
    assertThat(result.getFieldValue(VISIBILITY_VERSION), is(2L));
    assertThat(result.getFieldValue("updateInstant_basic"), is(this.in.toEpochMilli()));
    assertThat(result.getFieldValue(BRAND), is("brand"));
    assertThat(result.getFieldValue(MODEL), is("model"));
    assertThat(result.getFieldValue(CATEGORY), is("category"));
    assertThat(result.getField(TAGS).getValue(), is(Set.of("tags", "tag")));
    assertThat(result.getField(ENGINES).getValue(), is(Set.of("FLAT")));
    assertThat(result.getField(FUELS).getValue(), is(Set.of("ELECTRIC")));
    assertThat(result.getFieldValue(ENGINES_BLOB), is(this.gson.toJson(this.truckDO.getEngines())));
    assertThat(result.getField(PRICES).getValue(), is(Set.of(100L)));
    assertThat(result.getFieldValue(VEHICLE_HAS_PRICES), is(true));
    assertThat(result.getFieldValue(PRICES_BLOB), is(this.gson.toJson(this.truckDO.getPrices().get())));
    assertThat(result.getField(WHEELS_TYPE).getValue(), is(Set.of("SUMMER")));
    assertThat(result.getField(WHEELS_SIZE).getValue(), is(Set.of(14)));
    assertThat(result.getFieldValue(WHEELS_BLOB), is(this.gson.toJson(this.truckDO.getWheels())));
    assertThat(result.getField(TRANSMISSIONS_TYPE).getValue(), is(Set.of("MANUAL", "AUTOMATIC")));
    assertThat(result.getFieldValue(COLORS_BLOB), is(this.gson.toJson(this.truckDO.getColors())));
    assertThat(result.getField(COLORS_TYPE).getValue(), is(Set.of("METALLIC", "SOLID")));
    assertThat(result.getField(COLORS_NAME + "es-ES").getValue(), is(Set.of("amarillo", "rojo")));
    assertThat(result.getField(COLORS_NAME + "en-GB").getValue(), is(Set.of("yellow", "red")));
    assertThat(result.getFieldValue(TRUCK_TYPE), is("CEMENT"));
  }
}