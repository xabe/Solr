package com.xabe.spring.solr.infrastructure.application.util;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParallelStreamsTest {

  @Test
  public void exampleParallelOrderedNoShortCircuiting() throws Exception {
    final ExecutorService executor = Executors.newFixedThreadPool(10);

    Assertions.assertThrows(RuntimeException.class, () -> IntStream.range(0, 10).boxed().map(i -> CompletableFuture.supplyAsync(() -> {
      if (i != 9) {
        try {
          Thread.sleep(10000);
        } catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
        return i;
      } else {
        throw new RuntimeException();
      }
    }, executor)).collect(collectingAndThen(toList(), list -> ParallelStreams.allOfOrException(list, executor).join())));
  }

  @Test
  public void exampleParallelOrderedShortCircuiting() throws Exception {
    final ExecutorService executor = Executors.newFixedThreadPool(10);

    final List<Integer> result = IntStream.range(0, 10).boxed().map(i -> CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }
      return i;
    }, executor)).collect(collectingAndThen(toList(), list -> ParallelStreams.allOfOrException(list, executor).join()));

    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(10)));
  }

}