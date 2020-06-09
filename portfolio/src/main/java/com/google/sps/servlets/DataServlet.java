// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/** Servlet that handles comment data. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final String DELIMITER = ": "; 
  private static final String TEXT_TYPE = "text/html;";
  private static final String REDIRECT_URL = "/comments.html";
  private static final String CONTENT = "content";
  private static final String TIMESTAMP = "timestamp";
  private static final String NAME = "name";
  private static final String QUERY_STRING = "Comment";
  private static final int MILLI_DIVISOR = 1000;
  private static final int SECOND_DIVISOR = 60;
  private List<String> messages = new ArrayList<>();
  private QueryHelper queryHelper = new QueryHelper(QUERY_STRING);

  /** Get all comment entities in the datastore and add them to messages. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PreparedQuery results = queryHelper.getResults();
    messages.clear();

    for (Entity entity : results.asIterable()) {
      messages.add(formatEntityString(entity));
    }

    response.setContentType(TEXT_TYPE);
    String json = new Gson().toJson(messages);
    response.getWriter().println(json);
  }

  /** Get comment from form, and create and put comment entity into datastore. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the comment input from the form.
    String text = getParameter(request, "comment-input", "");
    messages.add(text);

    String name = getParameter(request, "name-input", "");
    // Respond with the result.
    response.setContentType(TEXT_TYPE);
    response.getWriter().println(text);

    Entity commentEntity = makeCommentEntity(text, name, System.currentTimeMillis());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect(REDIRECT_URL);
  }

  private Long timeFromSubmit(Long submitTime) {
    return (System.currentTimeMillis() - submitTime) / MILLI_DIVISOR / SECOND_DIVISOR;
  }

  private String formatEntityString(Entity entity) {
    String comment = (String) entity.getProperty(CONTENT);
    String name = (String) entity.getProperty(NAME);
    Long minutes = timeFromSubmit((Long) entity.getProperty(TIMESTAMP));
    return name + DELIMITER + comment + DELIMITER + minutes;
  }

  private Entity makeCommentEntity(String text, String name, long timestamp) {
    Entity commentEntity = new Entity(QUERY_STRING);
    commentEntity.setProperty(CONTENT, text);
    commentEntity.setProperty(TIMESTAMP, timestamp);
    commentEntity.setProperty(NAME, name);
    return commentEntity;
  }

  /**
   * Returns the request parameter, or the default value if the parameter
   * was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
