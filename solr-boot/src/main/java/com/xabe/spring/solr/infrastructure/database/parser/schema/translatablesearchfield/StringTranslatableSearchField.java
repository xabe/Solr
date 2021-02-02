package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import com.xabe.spring.solr.infrastructure.database.parser.schema.Params;
import org.apache.commons.lang3.StringUtils;

public class StringTranslatableSearchField extends TranslatableSearchField {

  private final String fieldKpi;

  public StringTranslatableSearchField(final String field, final String fieldKpi) {
    super(field);
    this.fieldKpi = fieldKpi;
  }

  @Override
  protected boolean validate(final Params params) {
    final boolean fieldQueryValidation = StringUtils.isNotEmpty(params.getQueryText());
    final boolean rangeQueryValidation = StringUtils.isNotEmpty(params.getLowerTerm()) && StringUtils.isNotEmpty(params.getUpperTerm());

    return fieldQueryValidation || rangeQueryValidation;
  }

  @Override
  protected String transformation(final String fieldName, final Params params) {
    final StringBuilder queryBuilder = new StringBuilder(this.field).append(COLON);

    if (StringUtils.isNotEmpty(params.getLowerTerm()) && StringUtils.isNotEmpty(params.getUpperTerm())) {
      final String expressionRange = this.createRange(params);
      queryBuilder.append(expressionRange);
      if (StringUtils.isNotEmpty(this.fieldKpi)) {
        queryBuilder.insert(0, BRACKET_OPEN).append(" OR " + this.fieldKpi + ":").append(expressionRange).append(BRACKET_CLOSE);
      }
      return queryBuilder.toString();
    }

    final String expression = params.isQuoted() ? StringUtils.wrap(params.getQueryText(), WRAP_WITH) : params.getQueryText();

    queryBuilder.append(expression).toString();

    if (StringUtils.isNotEmpty(this.fieldKpi)) {
      queryBuilder.insert(0, BRACKET_OPEN).append(" OR " + this.fieldKpi + ":").append(expression).append(BRACKET_CLOSE);
    }

    return queryBuilder.toString();
  }

  private String createRange(final Params params) {
    return new StringBuilder().append(params.isStartInclusive() ? LEFT_SQUARE_BRACKET : LEFT_CURLY_BRACKET).append(params.getLowerTerm())
        .append(TO).append(params.getUpperTerm()).append(params.isEndInclusive() ? RIGHT_SQUARE_BRACKET : RIGHT_CURLY_BRACKET).toString();
  }

}
