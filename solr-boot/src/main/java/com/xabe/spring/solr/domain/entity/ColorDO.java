package com.xabe.spring.solr.domain.entity;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.xabe.spring.solr.infrastructure.application.search.service.mapper.MappingContext;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.apache.commons.collections.CollectionUtils;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ColorDO implements DO {

  private final String id;

  private final ColorTypeDO type;

  private final List<TextDO> names;

  public String getName(final MappingContext mappingContext) {
    if (CollectionUtils.isNotEmpty(this.names)) {
      return this.names.stream().filter(mappingContext::testLocale).findFirst().map(TextDO::getText).filter(Objects::nonNull).orElse(EMPTY);
    }
    return EMPTY;
  }
}
