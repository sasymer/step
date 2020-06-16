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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //want to find time ranges that the meeting could happen

    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();

    //once you get times that work, go through each attendee to see
    // if they have an event at that time

    List<TimeRange> timesThatDoNotWork = new ArrayList<>();

    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      Set<String> peopleAtEvent = event.getAttendees();
      for (String attendee : attendees) {
        if (peopleAtEvent.contains(attendee)) {
          timesThatDoNotWork.add(eventTime);
        }
      }
    }

    Collections.sort(timesThatDoNotWork, TimeRange.ORDER_BY_START);
    for (TimeRange time : timesThatDoNotWork) {
      System.out.println(time.start() + " " + time.end());
    }
    System.out.println("\n");

    //try to condense into largest slots that don't work
    ArrayList<TimeRange> condensedDontWork = new ArrayList<>();
    ArrayList<Integer> timesList = new ArrayList<>();

    for (int i = 0; i < timesThatDoNotWork.size() - 1; i++) {
      TimeRange first = timesThatDoNotWork.get(i);
      TimeRange second = timesThatDoNotWork.get(i + 1);

      if (first.overlaps(second)) { //can condense
        int firstEnd = first.end();
        int secondEnd = second.end();
        int newStart = first.start();

        if (firstEnd > secondEnd) {
          newEnd = firstEnd;
        } else {
          newEnd = secondEnd;
        }
        timesList.add(newStart);
        timesList.add(newEnd);
        condensedDontWork.add(new TimeRange(newStart, newEnd - newStart));
      } else {
        condensedDontWork.add(first);
        timesList.add(first.start());
        timesList.add(first.end());
      }
    }

    int lowerBound = 0;
    int upperBound = 1440;

    Collection<TimeRange> timesThatWork = new ArrayList<>();

    for (int i = 0; i < condensedDontWork; i++) {
      int first = condensedDontWork.get(i).start();
      int second = condensedDontWork.get(i).end();
    }










    for (int i = 0; i < timesList.size(); i++) {
      int start = 0;
      if (timesList.get(i) == 0)
        start = timesList.get(i + 1);

      int end = 1440;
      if (i < timesList.size() - 1) {
        end = timesList.get(i + 1).start();
      }

      if (start == 0) {
        lowerBound = timesList.get(i).end();
      } else {
        TimeRange timeRange = new TimeRange(lowerBound, start - lowerBound);
      }

    }


    for (int i = 0; i < condensedDontWork.size(); i++) {
      TimeRange notWork = condensedDontWork.get(i);
      int upper = notWork.start();
      if (upper != lowerBound) {
        TimeRange work = new TimeRange(lowerBound, upper - lowerBound);
        timesThatWork.add(work);
        lowerBound = notWork.end();
      }

    }


    
    return timesThatWork;
   //throw new UnsupportedOperationException("TODO: Implement this method.");
  }
}
