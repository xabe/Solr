package com.xabe.spring.solr.infrastructure.database.parser;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.TAGS;

import com.xabe.spring.solr.infrastructure.database.parser.schema.SearchSchema;
import com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield.TranslatableSearchFieldConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolrQueryTranslator implements QueryTranslator {

  private static final String DEFAULT_FIELD = TAGS;

  private final Logger logger;

  private final SearchSchema searchSchema;

  @Autowired
  public SolrQueryTranslator(final Logger logger, final TranslatableSearchFieldConfiguration configuration) {
    this.logger = logger;
    this.searchSchema = new SearchSchema(configuration);
  }

  @Override
  public String translate(final String query) throws IllegalArgumentException {
    try {
      return StringUtils.isEmpty(query) ? query
          : this.checkProhibitedQuery(new SchemaSolrQueryParser(DEFAULT_FIELD, this.searchSchema).parse(query)).toString();
    } catch (final Exception ex) {
      this.logger.error("Failed to format {}", query, ex);
      throw new IllegalArgumentException(ex);
    }
  }

  private Query checkProhibitedQuery(final Query query) {
    return (query instanceof BooleanQuery) ? this.replaceProhibitedQueries((BooleanQuery) query) : query;
  }

  private BooleanQuery replaceProhibitedQueries(final BooleanQuery bq) {
    final BooleanQuery.Builder builder = new Builder();

    builder.setMinimumNumberShouldMatch(bq.getMinimumNumberShouldMatch());
    bq.clauses().forEach(c -> builder.add(this.replaceProhibitedQueries(c)));

    return builder.build();
  }

  private BooleanClause replaceProhibitedQueries(final BooleanClause bc) {
    BooleanClause result = bc;

    if (bc.isProhibited()) {
      result = this.wrapNegativeQuery(bc);
    } else if (bc.getQuery() instanceof BooleanQuery) {
      result = new BooleanClause(this.replaceProhibitedQueries((BooleanQuery) bc.getQuery()), bc.getOccur());
    }

    return result;
  }

  private BooleanClause wrapNegativeQuery(final BooleanClause c) {
    Query q = c.getQuery();
    if (q instanceof BooleanQuery) {
      q = this.replaceProhibitedQueries((BooleanQuery) q);
    }

    return new BooleanClause(
        new BooleanQuery.Builder().setMinimumNumberShouldMatch(0).add(new MatchAllDocsQuery(), Occur.MUST).add(q, Occur.MUST_NOT).build(),
        Occur.MUST);
  }

}
