package com.xabe.spring.solr.infrastructure.database.parser;

public interface QueryTranslator {

  String translate(final String query) throws IllegalArgumentException;

}
