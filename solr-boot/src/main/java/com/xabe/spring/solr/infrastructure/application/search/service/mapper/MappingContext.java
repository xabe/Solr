package com.xabe.spring.solr.infrastructure.application.search.service.mapper;

import com.xabe.spring.solr.domain.entity.TextDO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingContext {

  @NonNull
  private final String locale;

  public boolean testLocale(final TextDO text) {
    return this.locale.equalsIgnoreCase(text.getLocale());
  }

}
