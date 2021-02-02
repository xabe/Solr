package com.xabe.spring.solr.infrastructure.application.search.dto;

import java.io.Serializable;
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
public class SectionDTO implements Serializable {

  @Builder.Default
  private final List<VehicleDTO> elements = Collections.EMPTY_LIST;

  private final int sequenceNo;

}
