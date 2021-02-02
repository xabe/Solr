package com.xabe.spring.solr.infrastructure.application.search.dto;

import java.io.Serializable;
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
public class ParamsDTO implements Serializable {

  private final Long visibilityVersion;

  private final String storeId;

  private final String locale;

  private final boolean filterHasPrice;

}
