package com.xabe.spring.solr.infrastructure.database;

public final class SolrParams {

  public static final String DF = "df";

  public static final String Q = "q";

  public static final String FQ = "fq";

  public static final String FL = "fl";

  public static final String SORT = "sort";

  public static final String RAW = "raw";

  public static final String ROUTE = "_route_";

  public static final String ROUTE_ID_SEPARATOR = "!";

  public static final String CACHE_QUERY = "{!cache=false}";

  public static final String PATH_SELECT = "/select";

  public static final String PATH_GET = "/get";

  public static final String ALL_FIELDS = "*";

  public static final String CURSOR_MARK = "cursorMark";

  public static final String ID_SEPARATOR = "::";

  private SolrParams() {
  }

}
