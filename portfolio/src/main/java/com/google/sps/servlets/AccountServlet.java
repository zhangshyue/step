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

/** Servlet that handle user account status */
@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    private List<String> status;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        UserService userService = UserServiceFactory.getUserService();
        status = new ArrayList<>();
        Gson gson = new Gson();
        String json = new String();

        // Get login information of the user
        if (!userService.isUserLoggedIn()) {
            String loginUrl = userService.createLoginURL("/comment.html");
            status.add("Logout");
            status.add(loginUrl);
            json = gson.toJson(status);
        }else{
            String logoutUrl = userService.createLogoutURL("/comment.html");
            status.add("Login");
            status.add(getUserUsername(userService.getCurrentUser().getUserId()));
            status.add(logoutUrl);
            json = gson.toJson(status);
        }
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /**
    * Returns the username of the user with id, or empty String if the user has not set a username.
    */
    private String getUserUsername(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query =
            new Query("UserInfo")
                .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return "";
        }
        String username = (String) entity.getProperty("username");
        return username;
    }
}
