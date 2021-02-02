package com.xabe.spring.solr.acceptance.util;

import java.util.Iterator;

public class PaginationIterator implements Iterable<Pagination> {

  private final Pagination pagination;

  public PaginationIterator(final Pagination pagination) {
    this.pagination = pagination;
  }

  @Override
  public Iterator<Pagination> iterator() {
    return new Iterator<Pagination>() {
      @Override
      public boolean hasNext() {
        return PaginationIterator.this.pagination.hasNextPage();
      }

      @Override
      public Pagination next() {
        return PaginationIterator.this.pagination.nextPage();
      }
    };
  }
}
