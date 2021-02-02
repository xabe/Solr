package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.TAGS;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrParams.ALL_FIELDS;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TranslatableSearchFieldConfiguration {

  private final Map<String, TranslatableSearchField> translators;

  public TranslatableSearchFieldConfiguration() {
    this.translators = new HashMap<>();
    this.translators.put(ALL_FIELDS, new StringTranslatableSearchField(ALL_FIELDS, EMPTY));
    this.translators.put(TAGS, new StringTranslatableSearchField(TAGS, EMPTY));
    this.translators.put(VEHICLE_ID, new StringTranslatableSearchField(VEHICLE_ID, EMPTY));
    this.translators.put(VEHICLE_TYPE, new StringTranslatableSearchField(VEHICLE_TYPE, EMPTY));
  }

  public TranslatableSearchField getQuery(final String key) {
    return this.translators.getOrDefault(key, TranslatableSearchField.DEFAULT);
  }

}
