package com.xabe.spring.solr.infrastructure.database.parser.schema;

import com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield.TranslatableSearchFieldConfiguration;
import org.apache.lucene.search.Query;

public class SearchSchema {

  private final TranslatableSearchFieldConfiguration configuration;

  public SearchSchema(final TranslatableSearchFieldConfiguration configuration) {
    this.configuration = configuration;
  }

  public Query translateGetFieldQuery(final String fieldName, final String queryText, final boolean quoted) {
    final Params params = Params.builder().queryText(queryText).quoted(quoted).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

  public Query translateGetFuzzyQuery(final String fieldName, final String termStr, final float minSimilarity) {
    final Params params = Params.builder().queryText(termStr).minSimilarity(minSimilarity).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

  public Query translateGetPrefixQuery(final String fieldName, final String termStr) {
    final Params params = Params.builder().queryText(termStr).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

  public Query translateGetRangeQuery(final String fieldName, final String lowerTerm, final String upperTerm, final boolean startInclusive,
      final boolean endInclusive) {
    final Params params =
        Params.builder().lowerTerm(lowerTerm).upperTerm(upperTerm).startInclusive(startInclusive).endInclusive(endInclusive).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

  public Query translateGetRegexpQuery(final String fieldName, final String termStr) {
    final Params params = Params.builder().queryText(termStr).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

  public Query translateGetWildcardQuery(final String fieldName, final String termStr) {
    final Params params = Params.builder().queryText(termStr).build();
    return this.configuration.getQuery(fieldName).transformQuery(fieldName, params);
  }

}
