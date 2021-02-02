package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import com.xabe.spring.solr.infrastructure.application.util.TimeUtil;
import com.xabe.spring.solr.infrastructure.database.parser.schema.Params;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.StringUtils;

public class RangeDayStringTranslatableSearchField extends TranslatableSearchField {

  public RangeDayStringTranslatableSearchField(final String field) {
    super(field);
  }

  @Override
  protected boolean validate(final Params params) {
    final boolean fieldQueryValidation = StringUtils.isNotEmpty(params.getQueryText());
    return fieldQueryValidation && this.validateDaysParams(params.getQueryText());
  }

  @Override
  protected String transformation(final String fieldName, final Params params) {
    final Instant instant = TimeUtil.getNormalizedInstant();
    final StringBuilder queryBuilder = new StringBuilder(this.field).append(COLON);
    return queryBuilder.append(LEFT_SQUARE_BRACKET).append(instant.toEpochMilli() - (Duration.parse(params.getQueryText()).toMillis()))
        .append(TO).append(ASTERISK).append(RIGHT_SQUARE_BRACKET).toString();
  }

  private boolean validateDaysParams(final String paramDays) {
    try {
      Duration.parse(paramDays);
      return true;
    } catch (final DateTimeParseException e) {
      return false;
    }
  }

}
