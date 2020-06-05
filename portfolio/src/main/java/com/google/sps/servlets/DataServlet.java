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

  private List<String> messages = new ArrayList<>();
  private static final String SPACE = " ";
  private String numberChoiceString = "";

  /** Get all comment entities in the datastore and add them to messages. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("DO GET");
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query); 

    // int numberComments = getNumberComments(request);
    // System.out.println("NUM COMMENTS = " + numberComments);
    // if (numberComments == -1) {
    //  return;
    // }

    // Add "numberComments" comments to messages list from the datastore.
    messages.clear();
    int count = 0;
    //Long numComments = new Long(0);
  
    for (Entity entity : results.asIterable()) {
      // if (count == 0) {
      //   numComments = (Long) entity.getProperty("numComments");
      // }
      // if (count >= numComments) {
      //   break;
      // }
      String comment = (String) entity.getProperty("content");
      String name = (String) entity.getProperty("name");
      messages.add(name + ": " + comment);
      count++;
    }

    response.setContentType("text/html;");
    String json = new Gson().toJson(messages);
    response.getWriter().println(json);
  }

  /** Get comment from form, and create and put comment entity into datastore. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the comment input from the form.
    System.out.println("DO POST");
    String text = getParameter(request, "comment-input", "");
    messages.add(text);

    String name = getParameter(request, "name-input", "");
    // String numComments = getParameter(request, "n-comments", "1");
    // int nComments = 0;
    // try {
    //   nComments = Integer.parseInt(numComments);
    // } catch (NumberFormatException e) {
    //   System.err.println("Number format exception: " + numComments);
    // }

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(text);

    int nComments = 10; //TODO: get rid of later
    Entity commentEntity = makeCommentEntity(text, name, System.currentTimeMillis(), nComments);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/comments.html");
  }

  private Entity makeCommentEntity(String text, String name, long timestamp, int nComments) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", text);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("numComments", 10);
    return commentEntity;
  }

  /**
   * Returns the request parameter, or the default value if the parameter
   * was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    System.out.println("Value " + value + " name " + name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private int getNumberComments(HttpServletRequest request) {
    // Get the input from the form. 
    String param = request.getParameter("n-comments");
    if (param != null)
      numberChoiceString = param;

    // Convert the input to an int.
    int numberChoice;
    try {
      numberChoice = Integer.parseInt(numberChoiceString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numberChoiceString);
      return -1;
    }

    if (numberChoice < 0 || numberChoice > 10) {
      System.err.println("Choice is out of range: " + numberChoiceString);
      return -1;
    }

    return numberChoice;
  }

}
