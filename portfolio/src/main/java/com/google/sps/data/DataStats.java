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

package com.google.sps.data;

import java.util.Date;

/** Class containing server statistics. */
public final class DataStats {

  private final String name;
  private final String comment;
  private final Date commentTime;
  private final int upvote;
  private final long id;
  private final String imgUrl;
  private final int rating;

  public DataStats(String name, String comment, Date commentTime, int upvote, long id, String imgUrl, int rating) {
    this.name = name;
    this.comment = comment;
    this.commentTime = commentTime;
    this.upvote = upvote;
    this.id = id;
    this.imgUrl = imgUrl;
    this.rating = rating;
  }

  public String getName() {
    return name;
  }

  public String getComment() {
    return comment;
  }

  public Date getCommentTime() {
    return commentTime;
  }

  public int getUpvote() {
    return upvote;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public int getRating() {
    return rating;
  }
}
