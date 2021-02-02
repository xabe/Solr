package com.xabe.spring.solr.acceptance.util;

public final class Pagination {

  private final int total;

  private final int sizePage;

  private int limit;

  private int page;

  private int skip;

  private Pagination(final int sizePage, final int total) {
    this.total = total;
    this.sizePage = sizePage;
    this.page = 0;
    this.skip = 0;
    this.limit = sizePage;
  }

  private Pagination(final int sizePage, final int total, final int page, final int skip, final int limit) {
    this.total = total;
    this.sizePage = sizePage;
    this.page = page;
    this.skip = skip;
    this.limit = limit;
  }

  public static Pagination of(final int limit, final int total) {
    return new Pagination(limit, total);
  }

  public boolean hasNextPage() {
    return this.total >= this.page * this.sizePage;
  }

  public Pagination nextPage() {
    final Pagination pagination = new Pagination(this.sizePage, this.total, this.page, this.skip, this.limit);
    ++this.page;
    this.skip += this.sizePage;
    this.limit += this.sizePage;
    return pagination;
  }

  public int getSkip() {
    return this.skip;
  }

  public int getLimit() {
    return this.limit;
  }

  public int getNumberPage() {
    return Math.round((float) (this.total / this.sizePage)) + 1;
  }
}
