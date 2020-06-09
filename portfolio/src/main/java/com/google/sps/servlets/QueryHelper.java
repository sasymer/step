package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** 
  This class handles query operations to the datastore.
*/
public class QueryHelper {

  private static final String TIMESTAMP = "timestamp";
  private Query myQuery;
  private DatastoreService myDatastore;
  private PreparedQuery myResults;
  private String queryString;

  public QueryHelper(String queryString) {
    this.queryString = queryString;
    myQuery = new Query(queryString).addSort(TIMESTAMP, SortDirection.DESCENDING);
    myDatastore = DatastoreServiceFactory.getDatastoreService();
    myResults = myDatastore.prepare(myQuery);
  }

  public PreparedQuery getResults() {
    return myResults;
  }

  public void deleteAllEntries() {
    for (Entity entity : myResults.asIterable()) {
      Key key = entity.getKey();
      myDatastore.delete(key);
    }
  }

}