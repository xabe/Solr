package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import com.xabe.spring.solr.infrastructure.database.parser.schema.Params;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

public class StringListTranslatableSearchField extends TranslatableSearchField {

  private static final String OR = " OR ";

  private final String fieldKpi;

  public StringListTranslatableSearchField(final String field, final String fieldKpi) {
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
    final StringBuilder queryBuilder = new StringBuilder(BRACKET_OPEN).append(this.field).append(COLON);

    final String expression = params.isQuoted() ? StringUtils.wrap(params.getQueryText(), WRAP_WITH) : params.getQueryText();

    final Iterator<String> paramList = Arrays.stream(expression.split(",")).iterator();

    this.populateMultiParamQuery(queryBuilder, expression, paramList);

    return queryBuilder.append(BRACKET_CLOSE).toString();
  }

  private void populateMultiParamQuery(final StringBuilder queryBuilder, final String expression, final Iterator<String> paramList) {
    while (paramList.hasNext()) {
      queryBuilder.append(paramList.next().trim());
      if (StringUtils.isNotEmpty(this.fieldKpi)) {
        queryBuilder.append(OR).append(this.fieldKpi).append(COLON).append(expression);
      }
      if (paramList.hasNext()) {
        queryBuilder.append(OR).append(this.field).append(COLON);
      }
    }
  }

}
