package com.xabe.spring.solr.infrastructure.application.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

  @Test
  public void givenAUpdateTimestampNullWhenInvokeGetMillisThenReturnCurrent() throws Exception {
    //Given
    final Long updateTimestamp = null;
    //When
    final long result = TimeUtil.getMillis(updateTimestamp);

    //Then
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void givenAUpdateTimestampMaxWhenInvokeGetMillisThenReturnTrunc() throws Exception {
    //Given
    final Long updateTimestamp = 10000000000001L;
    //When
    final long result = TimeUtil.getMillis(updateTimestamp);

    //Then
    assertThat(result, is(1000000000000L));
  }

  @Test
  public void givenAUpdateTimestampWhenInvokeGetMillisThenReturnMillis() throws Exception {
    //Given
    final Long updateTimestamp = 1000000000000L;
    //When
    final long result = TimeUtil.getMillis(updateTimestamp);

    //Then
    assertThat(result, is(1000000000000L));
  }

  @Test
  public void shouldGetCurrentMillisNormalize() throws Exception {
    //Given

    //When
    final Instant result = TimeUtil.getNormalizedInstant();

    //Then
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void shouldGetInstantMillisNormalize() throws Exception {
    //Given

    //When
    final Instant result = TimeUtil.getNormalizedInstant(1000000000000L);

    //Then
    assertThat(result, is(notNullValue()));
  }

}