package com.xabe.spring.solr.infrastructure.database.parser.schema;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Params {

  private final String queryText;

  private final boolean quoted;

  private final String termStr;

  private final float minSimilarity;

  private final String lowerTerm;

  private final String upperTerm;

  private final boolean startInclusive;

  private final boolean endInclusive;
}
