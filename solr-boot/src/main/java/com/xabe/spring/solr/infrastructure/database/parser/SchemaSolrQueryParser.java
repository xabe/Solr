package com.xabe.spring.solr.infrastructure.database.parser;

import com.xabe.spring.solr.infrastructure.database.parser.schema.SearchSchema;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.search.Query;

public class SchemaSolrQueryParser extends SolrQueryParser {

  private final SearchSchema schema;

  public SchemaSolrQueryParser(final String defaultField, final SearchSchema schema) {
    super(defaultField, new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(final String fieldName) {
        return new TokenStreamComponents(new ClassicTokenizer());
      }
    });
    this.schema = schema;
  }

  @Override
  protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) {
    return this.schema.translateGetFieldQuery(field, queryText, quoted);
  }

  @Override
  protected Query getFuzzyQuery(final String field, final String termStr, final float minSimilarity) {
    return this.schema.translateGetFuzzyQuery(field, termStr, minSimilarity);
  }

  @Override
  protected Query getPrefixQuery(final String field, final String termStr) {
    return this.schema.translateGetPrefixQuery(field, termStr);
  }

  @Override
  protected Query getRangeQuery(final String field, final String lowerTerm, final String upperTerm, final boolean startInclusive,
      final boolean endInclusive) {
    return this.schema.translateGetRangeQuery(field, lowerTerm, upperTerm, startInclusive, endInclusive);
  }

  @Override
  protected Query getRegexpQuery(final String field, final String termStr) {
    return this.schema.translateGetRegexpQuery(field, termStr);
  }

  @Override
  protected Query getWildcardQuery(final String field, final String termStr) {
    return this.schema.translateGetWildcardQuery(field, termStr);
  }

}
