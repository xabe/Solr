package com.xabe.spring.solr.domain.exception;

public class RepositoryException extends RuntimeException {

  public RepositoryException(final Exception e) {
    super(e);
  }
}

