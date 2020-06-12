package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private class Login {
    private String login;
    private String url;
    private String email;

    public Login() {
      login = "false";
    }

    public void loggedIn() {
      login = "true";
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/comments.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      
      Login login = new Login();
      login.loggedIn();
      login.setUrl(logoutUrl);
      login.setEmail(userEmail);

      String json = new Gson().toJson(login);
      response.getWriter().println(json);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/comments.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      Login login = new Login();
      login.setUrl(loginUrl);

      String json = new Gson().toJson(login);
      response.getWriter().println(json);      
    }
  }
}
