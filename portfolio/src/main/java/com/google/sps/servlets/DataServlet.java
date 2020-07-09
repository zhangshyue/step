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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.DataStats;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Entry").addSort("commentTime", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        int commentsNumber = Integer.parseInt(request.getParameter("number"));
        
        List<DataStats> comments = new ArrayList<>();
        int num = 0;
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String comment = (String) entity.getProperty("comment");
            Date commentTime = (Date) entity.getProperty("commentTime");
            int upvote = ((Long) entity.getProperty("upvote")).intValue();
            String imgUrl = (String) entity.getProperty("imgUrl");
            int rating = Integer.parseInt(entity.getProperty("rating").toString());

            comments.add(new DataStats(name, comment, commentTime, upvote, id, imgUrl, rating));
            num += 1;
            if (num == commentsNumber) {
                break;
            }
        }

        String json = convertToJsonUsingGson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /**
    * Converts a ServerStats instance into a JSON string using the Gson library.
    */
    private String convertToJsonUsingGson(List<DataStats> comments) {
        Gson gson = new Gson();
        return gson.toJson(comments);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();

        // If user is not logged in, show a login form 
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/username");
            return;
        }
        String username = getUsername(userService.getCurrentUser().getUserId());
        if (username.isEmpty()) {
            username = "Anonymous";
        }
   
        // Get the input from the form.
        String name = username;
        String comment = getParameter(request, "comment", "");
        Date currentTime = new Date();
        String imageUrl = getUploadedFileUrl(request, "image");
        String rating = request.getParameter("number-rating");
        if (imageUrl == null) {
            imageUrl = "";
        }

        Entity commentEntity = new Entity("Entry");
        commentEntity.setProperty("name", name);
        commentEntity.setProperty("comment", comment);
        commentEntity.setProperty("commentTime", currentTime);
        commentEntity.setProperty("upvote", 0);
        commentEntity.setProperty("imgUrl", imageUrl);
        commentEntity.setProperty("rating", rating);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/comment.html");
    }
    
    /**
    * @return the request parameter, or the default value if the parameter
    *         was not specified by the client
    */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /** Returns the username of the user with id, or null if the user has not set a username. */
    private String getUsername(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return null;
        }
        String username = (String) entity.getProperty("username");
        return username;
    }

    /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
    private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("image");

        // User submitted form without selecting a file, so we can't get a URL. (dev server)
        if (blobKeys == null || blobKeys.isEmpty()) {
            return null;
        }

        // Our form only contains a single file input, so get the first index.
        BlobKey blobKey = blobKeys.get(0);

        // User submitted form without selecting a file, so we can't get a URL. (live server)
        // BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
        // if (blobInfo.getSize() == 0) {
        //     blobstoreService.delete(blobKey);
        //     return null;
        // }

        // Use ImagesService to get a URL that points to the uploaded file.
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

        // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
        // path to the image, rather than the path returned by imagesService which contains a host.
        try {
            URL url = new URL(imagesService.getServingUrl(options));
            return url.getPath();
        } catch (MalformedURLException e) {
            return imagesService.getServingUrl(options);
        }
    }
}


