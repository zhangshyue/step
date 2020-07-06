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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    private List<String> status;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();
    status = new ArrayList<>();

    // If user is not logged in, show a login form (could also redirect to a login page)
    if (!userService.isUserLoggedIn()) {
    //   String loginUrl = userService.createLoginURL("/account");
    //   out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      response.setContentType("text/html;");
    response.getWriter().println("Not login");
      return;
    }else{
        String logoutUrl = userService.createLogoutURL("/account");
        Gson gson = new Gson();
        status.add("Login");
        status.add(logoutUrl);
    String json = gson.toJson(status);
        response.setContentType("application/json;");
    response.getWriter().println(json);
    //     response.setContentType("text/html;");
    // response.getWriter().println("Login");
      return;
    }

    // If user has not set a username, redirect to username page
    // String username = getUserUsername(userService.getCurrentUser().getUserId());
    // if (username == null) {
    //   response.sendRedirect("/username");
    //   return;
    // }

    // // User is logged in and has a username, so the request can proceed
    // String logoutUrl = userService.createLogoutURL("/account");
    // out.println("<h1>Home</h1>");
    // out.println("<p>Hello " + username + "!</p>");
    // out.println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    // out.println("<p>Change your username <a href=\"/username\">here</a>.</p>");
    // out.println("<p>Get to comments <a href=\"/comment.html\">here</a>.</p>");
  }

  /** Returns the username of the user with id, or null if the user has not set a username. */
  private String getUserUsername(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String username = (String) entity.getProperty("username");
    return username;
  }
}
