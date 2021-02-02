package com.xabe.spring.solr.domain.exception;

public class VehicleSectionConcurrentException extends RuntimeException {

  public VehicleSectionConcurrentException(final Exception e) {
    super(e);
  }
}
