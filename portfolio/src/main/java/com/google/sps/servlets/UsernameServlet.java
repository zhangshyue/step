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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handle login and set username */
@WebServlet("/username")
public class UsernameServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>Set Username</h1>");
        // Form that set username
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
        out.println("<p>Set your username here:</p>");
        out.println("<form method=\"POST\" action=\"/username\">");
        out.println("<input name=\"username\" />");
        out.println("<br/>");
        out.println("<button>Submit</button>");
        out.println("</form>");
        } else {
        String loginUrl = userService.createLoginURL("/username");
        out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
        response.sendRedirect("/username");
        return;
        }

        String username = request.getParameter("username");
        String id = userService.getCurrentUser().getUserId();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("UserInfo", id);
        entity.setProperty("id", id);
        entity.setProperty("username", username);
        datastore.put(entity);

        response.sendRedirect("/comment.html");
    }
}
