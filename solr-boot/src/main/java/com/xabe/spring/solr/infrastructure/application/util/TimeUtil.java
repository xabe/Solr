package com.xabe.spring.solr.infrastructure.application.util;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

  private static final Long MAX_MILLIS = 10000000000000L;

  private static final double MAX_MILLIS_COUNT = MAX_MILLIS.toString().length() - 1d;

  public static long getMillis(final Long updateTimestamp) {
    if (Objects.isNull(updateTimestamp)) {
      return System.currentTimeMillis();
    }

    return updateTimestamp < MAX_MILLIS ? updateTimestamp
        : (long) (updateTimestamp / Math.pow(10, updateTimestamp.toString().length() - MAX_MILLIS_COUNT));
  }

  public static Instant getNormalizedInstant() {
    return getNormalizedInstant(System.currentTimeMillis());
  }

  public static Instant getNormalizedInstant(final long currentTimestamp) {
    return Instant.ofEpochMilli(currentTimestamp - (currentTimestamp % TimeUnit.MINUTES.toMillis(10)));
  }
}
