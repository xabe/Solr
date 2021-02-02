package com.xabe.spring.solr.infrastructure.application.search.dto;

import java.io.Serializable;
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
public class DefinitionDTO implements Serializable {

  private final String filter;

  private final List<SectionDefinitionDTO> sectionDefinitions;

}
