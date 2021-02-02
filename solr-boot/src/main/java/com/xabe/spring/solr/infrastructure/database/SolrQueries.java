package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.GROUP_ID_FIELD;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.STORE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_HAS_PRICES;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_ID;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;
import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_VERSION;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class SolrQueries {

  public static final String SEPARATOR = ":";

  public static final String SEPARATOR_BRACKET = ":(";

  public static final String BRACKET_OPEN = "(";

  public static final String BRACKET_CLOSE = ")";

  public static final String OPERATOR_AND = " AND ";

  public static final String OPERATOR_AND_NOT = OPERATOR_AND + "NOT ";

  public static final String OPERATOR_OR = " OR ";

  public static final String THIS_WILL_NEVER_MATCH = "THIS_WILL_NEVER_MATCH";

  public static final String VEHICLE_TYPE_QUERY = " AND vehicleType: (CAR OR TRUCK)";

  public static final String COLLAPSE_FQ = "{!collapse field=" + GROUP_ID_FIELD + " max=" + VEHICLE_VERSION + "}";

  public static final String QUERY_HAS_PRICES = VEHICLE_HAS_PRICES + ":(true)";

  public static final BiFunction<String, String, String> QUERY_VEHICLE_ID =
      (id, type) -> BRACKET_OPEN + VEHICLE_ID + SEPARATOR + id + OPERATOR_AND + VEHICLE_TYPE + SEPARATOR + type + BRACKET_CLOSE;

  public static final Function<String, String> QUERY_STORE_ID = storeId -> STORE_ID + SEPARATOR_BRACKET + storeId + BRACKET_CLOSE;

}
