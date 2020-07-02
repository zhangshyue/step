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
import com.google.sps.data.DataStats;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handle comments data */
@WebServlet("/update")
public class UpvoteServlet extends HttpServlet {
    // @Override
    // public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     // Get the input from the form.
    //     String name = getParameter(request, "name", "Anonymous");
    //     String comment = getParameter(request, "comment", "");
    //     Date currentTime = new Date();

    //     Entity commentEntity = new Entity("Entry");
    //     commentEntity.setProperty("name", name);
    //     commentEntity.setProperty("comment", comment);
    //     commentEntity.setProperty("commentTime", currentTime);
    //     commentEntity.setProperty("upvote", 0);

    //     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //     datastore.put(commentEntity);

    //     // Redirect back to the HTML page.
    //     response.sendRedirect("/comment.html");
    // }
    
    // /**
    // * @return the request parameter, or the default value if the parameter
    // *         was not specified by the client
    // */
    // private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    //     String value = request.getParameter(name);
    //     if (value == null) {
    //     return defaultValue;
    //     }
    //     return value;
 
    // }
}