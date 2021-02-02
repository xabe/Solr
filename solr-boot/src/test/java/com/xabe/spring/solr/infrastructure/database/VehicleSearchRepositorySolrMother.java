package com.xabe.spring.solr.infrastructure.database;

import static com.xabe.spring.solr.infrastructure.database.SolrFields.VEHICLE_TYPE;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

public class VehicleSearchRepositorySolrMother {

  public static QueryResponse createSolrQueryResponse(final String type) {
    final SolrDocument solrDocument = new SolrDocument();
    solrDocument.setField(VEHICLE_TYPE, type);
    final SolrDocumentList documents = new SolrDocumentList();
    documents.add(solrDocument);
    final NamedList namedList = new NamedList();
    namedList.add("response", documents);
    final QueryResponse response = new QueryResponse();
    response.setResponse(namedList);
    return response;
  }
}
