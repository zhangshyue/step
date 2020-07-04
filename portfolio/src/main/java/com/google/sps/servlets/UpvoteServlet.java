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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.sps.data.DataStats;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handle upvotes */
@WebServlet("/update-upvote")
public class UpvoteServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = Long.parseLong(request.getParameter("id"));
        Key key = KeyFactory.createKey("Entry", id);
        // Update the number of upvotes of the comment that is been clicked.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity commentEntity = datastore.get(key);
            int upvote = Integer.parseInt(commentEntity.getProperty("upvote").toString()) + 1;
            commentEntity.setProperty("upvote", upvote);
            datastore.put(commentEntity);
        }catch (EntityNotFoundException e) {
            return;
        }
    }
}