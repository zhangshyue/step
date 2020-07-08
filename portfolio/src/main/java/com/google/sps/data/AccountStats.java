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

/** Class containing user account statistics. */
public final class AccountStats {

  private final String name;
  private final String status;
  private final String url;

  public AccountStats(String name, String status, String url) {
    this.name = name;
    this.status = status;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getUrl() {
    return url;
  }
}
