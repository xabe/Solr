package com.xabe.spring.solr.domain.exception;

public class VehicleConcurrentUpdateException extends RuntimeException {

  public VehicleConcurrentUpdateException(final String message) {
    super(message);
  }
}
