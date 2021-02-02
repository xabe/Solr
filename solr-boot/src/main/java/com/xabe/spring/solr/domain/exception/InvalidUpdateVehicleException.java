package com.xabe.spring.solr.domain.exception;

public class InvalidUpdateVehicleException extends RuntimeException {

  public InvalidUpdateVehicleException(final RuntimeException e) {
    super(e);
  }

  public InvalidUpdateVehicleException(final String message) {
    super(message);
  }
}
