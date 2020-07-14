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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.lang.Math;
import java.io.PrintStream;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return Arrays.asList();
        }
        if (events.size() == 0) {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }
        List<String> allAttendees = new ArrayList<>();
        allAttendees.addAll(request.getAttendees());
        allAttendees.addAll(request.getOptionalAttendees());

        List<TimeRange> timeRangesIngoreOptional = new ArrayList<>();
        List<TimeRange> timeRangesIncludeOptional = new ArrayList<>();
        for (Event item : events) {
            for (String s : request.getAttendees()) {
                if (item.getAttendees().contains(s)) {
                    timeRangesIngoreOptional.add(item.getWhen());
                    break;
                }
            }
            for (String s : allAttendees) {
                if (item.getAttendees().contains(s)) {
                    timeRangesIncludeOptional.add(item.getWhen());
                    break;
                }
            }
        }

        Collection<TimeRange> ignoreOptional = availableTimeRange(timeRangesIngoreOptional, request);
        Collection<TimeRange> includeOptional = availableTimeRange(timeRangesIncludeOptional, request);
        if (request.getAttendees().size() == 0) {
            return includeOptional;
        }
        if (includeOptional.size() != 0) {
            return includeOptional;
        }
        return ignoreOptional;
    }

    // return timeRanges that satisfy meeting duration from all available timeRanges
    public Collection<TimeRange> availableTimeRange(List<TimeRange> timeRanges, MeetingRequest request) {
        Collections.sort(timeRanges, TimeRange.ORDER_BY_START);
        int len = timeRanges.size();
        if (len == 0) {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }
        if (len == 1) {
            return Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, timeRanges.get(0).start(), false),
                TimeRange.fromStartEnd(timeRanges.get(0).end(), TimeRange.END_OF_DAY, true));
        }
        TimeRange pre = timeRanges.get(0);
        int index = 1;
        // Merge timerange that overlap
        while (index < len) {
            if (index == timeRanges.size()) {
                break;
            }
            if (pre.overlaps(timeRanges.get(index))) {
                int start = pre.start();
                int end = Math.max(pre.end(), timeRanges.get(index).end());
                timeRanges.remove(index);
                timeRanges.remove(index - 1);
                timeRanges.add(index - 1, TimeRange.fromStartEnd(start, end, false));
                pre = timeRanges.get(index - 1);
                len -= 1;
            } else {
                index += 1;
            }
        }
            List<TimeRange> queries = new ArrayList<>();
            int start = TimeRange.START_OF_DAY;
        for (int i = 0; i < timeRanges.size(); i++) {
            if (timeRanges.get(i).start() - start >= request.getDuration()) {
                queries.add(TimeRange.fromStartEnd(start, timeRanges.get(i).start(), false));
            }
            start = timeRanges.get(i).end();
        }
        if (TimeRange.END_OF_DAY - timeRanges.get(timeRanges.size()-1).end() >= request.getDuration()) {
            queries.add(TimeRange.fromStartEnd(timeRanges.get(timeRanges.size()-1).end(), TimeRange.END_OF_DAY, true));
        }
        return queries;
    }
}
