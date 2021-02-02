package com.xabe.spring.solr.infrastructure.database.parser.schema.translatablesearchfield;

import com.xabe.spring.solr.infrastructure.database.parser.schema.Params;

public class BooleanTranslatableSearchField extends TranslatableSearchField {

  public BooleanTranslatableSearchField(final String field) {
    super(field);
  }

  @Override
  protected boolean validate(final Params params) {
    return TRUE.equalsIgnoreCase(params.getQueryText()) || FALSE.equalsIgnoreCase(params.getQueryText());
  }

  @Override
  protected String transformation(final String fieldName, final Params params) {
    return TRUE.equalsIgnoreCase(params.getQueryText()) ? super.field : this.not(super.field);
  }

  private String not(final String query) {
    return new StringBuilder(DASH).append(BRACKET_OPEN).append(query).append(BRACKET_CLOSE).toString();
  }

}
