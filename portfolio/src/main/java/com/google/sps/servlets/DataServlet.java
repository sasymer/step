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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comment data. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final String DELIMITER = ": "; 
  private static final String TEXT_TYPE = "text/html;";
  private static final String REDIRECT_URL = "/comments.html";
  private static final String CONTENT = "content";
  private static final String TIMESTAMP = "timestamp";
  private static final String NAME = "name";
  private static final String EMAIL = "email";
  private static final String QUERY_STRING = "Comment";
  private List<String> messages = new ArrayList<>();
  private QueryHelper queryHelper = new QueryHelper();

  /** Get all comment entities in the datastore and add them to messages. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PreparedQuery results = queryHelper.getResults();
    messages.clear();

    String languageCode = request.getParameter("language");
    System.out.println("Lang code = " + languageCode);

    for (Entity entity : results.asIterable()) {
      messages.add(formatEntityString(entity, languageCode));
    }

    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
   
    String json = new Gson().toJson(messages);
    response.getWriter().println(json);
  }

  /** Get comment from form, and create and put comment entity into datastore. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = getParameter(request, "comment-input", "");
    String name = getParameter(request, "name-input", "");

    messages.add(text);
    
    // Respond with the result
    response.setContentType(TEXT_TYPE);
    response.getWriter().println(text);

    Entity commentEntity = makeCommentEntity(text, name, System.currentTimeMillis());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect(REDIRECT_URL);
  }

  private Long timeFromSubmit(Instant commentTime) {
    Duration timeSinceCommentPost = Duration.between(commentTime, Instant.now());
    return timeSinceCommentPost.toMinutes();
  }

  private String formatEntityString(Entity entity, String languageCode) {
    String comment = (String) entity.getProperty(CONTENT);

    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation = translate.translate(comment, Translate.TranslateOption.targetLanguage(languageCode));
    comment = translation.getTranslatedText();

    String name = (String) entity.getProperty(NAME);

    Instant commentTime = Instant.ofEpochMilli((Long) entity.getProperty(TIMESTAMP));
    Long minutes = timeFromSubmit(commentTime);

    String email = (String) entity.getProperty(EMAIL);
    return name + DELIMITER + comment + DELIMITER + minutes + DELIMITER + email;
  }

  private Entity makeCommentEntity(String text, String name, long timestamp) {
    Entity commentEntity = new Entity(QUERY_STRING);
    commentEntity.setProperty(CONTENT, text);
    commentEntity.setProperty(TIMESTAMP, timestamp);
    commentEntity.setProperty(NAME, name);
    commentEntity.setProperty(EMAIL, getCurrentUserEmail());

    return commentEntity;
  }

  private String getCurrentUserEmail() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.getCurrentUser().getEmail();
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
