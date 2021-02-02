package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import com.xabe.spring.solr.infrastructure.database.parser.schema.Params;
import com.xabe.spring.solr.infrastructure.database.parser.schema.TranslatedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;

public abstract class TranslatableSearchField {

  protected static final String TRUE = "true";

  protected static final String FALSE = "false";

  protected static final String DASH = "-";

  protected static final String BRACKET_OPEN = "(";

  protected static final String BRACKET_CLOSE = ")";

  protected static final String COLON = ":";

  protected static final String ASTERISK = "*";

  protected static final String TO = " TO ";

  protected static final String LEFT_SQUARE_BRACKET = "[";

  protected static final String RIGHT_SQUARE_BRACKET = "]";

  protected static final String LEFT_CURLY_BRACKET = "{";

  protected static final String RIGHT_CURLY_BRACKET = "}";

  protected static final String WRAP_WITH = "\"";

  static final TranslatableSearchField DEFAULT = new TranslatableSearchField(StringUtils.EMPTY) {
    @Override
    protected boolean validate(final Params params) {
      return false;
    }

    @Override
    protected String transformation(final String fieldName, final Params params) {
      return StringUtils.EMPTY;
    }
  };

  protected final String field;

  public TranslatableSearchField(final String field) {
    this.field = field;
  }

  protected abstract boolean validate(final Params params);

  protected abstract String transformation(final String fieldName, final Params params);

  public Query transformQuery(final String fieldName, final Params params) {
    if (!this.validate(params)) {
      throw new IllegalArgumentException(String.format("Illegal values for fieldName: %s and params: %s", fieldName, params.toString()));
    }
    return TranslatedQuery.of(this.transformation(this.field, params));
  }

}
