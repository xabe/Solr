package com.xabe.spring.solr.infrastructure.database.parser.schema;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.apache.lucene.search.Query;

@AllArgsConstructor
@EqualsAndHashCode
public class TranslatedQuery extends Query {

  private final String strValue;

  public static Query of(final String value) {
    return new TranslatedQuery(value);
  }

  @Override
  public String toString(final String field) {
    return this.strValue;
  }

}
