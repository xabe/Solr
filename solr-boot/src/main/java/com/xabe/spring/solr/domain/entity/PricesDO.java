package com.xabe.spring.solr.domain.entity;

import java.util.Collections;
import java.util.List;
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
public class PricesDO implements DO {

  private final String currencyCode;

  @Builder.Default
  private final List<PriceDO> prices = Collections.emptyList();
}
