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

  private List<TimeRange> getTimesThatDontWork(Collection<Event> events, Collection<String> attendees) {
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

    return timesThatDoNotWork;
  } 

  private List<TimeRange> condenseTimeRanges(List<TimeRange> timesThatDoNotWork) {
    ArrayList<TimeRange> condensedDontWork = new ArrayList<>();
    for (int i = 0; i < timesThatDoNotWork.size(); i++) {
      TimeRange first = timesThatDoNotWork.get(i);

      if (i == timesThatDoNotWork.size() - 1) {
        condensedDontWork.add(first);
        break;
      }
      TimeRange second = timesThatDoNotWork.get(i + 1);

      if (first.overlaps(second)) { //can condense
        int firstEnd = first.end();
        int secondEnd = second.end();
        int newStart = first.start();
        int newEnd = 0;

        if (firstEnd > secondEnd) {
          newEnd = firstEnd;
        } else {
          newEnd = secondEnd;
        }

        i++;
        condensedDontWork.add(TimeRange.fromStartDuration(newStart, newEnd - newStart));
      } else {
        condensedDontWork.add(first);
      }
    }
    return condensedDontWork;
  }

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();
    if (duration > 1440) {
      return new ArrayList<TimeRange>();
    }

    List<TimeRange> timesThatDoNotWork = getTimesThatDontWork(events, attendees);
    Collections.sort(timesThatDoNotWork, TimeRange.ORDER_BY_START);

    //Condense times that are not available
    List<TimeRange> condensedDontWork = condenseTimeRanges(timesThatDoNotWork);

    int lowerBound = 0;
    Collection<TimeRange> timesThatWork = new ArrayList<>();
    if (condensedDontWork.size() == 0) {
      timesThatWork.add(TimeRange.fromStartDuration(0, 1440));
    }

    for (int i = 0; i < condensedDontWork.size(); i++) {
      int first = condensedDontWork.get(i).start();
      int second = condensedDontWork.get(i).end();

      if (first == 0) {
        lowerBound = second;
        continue;
      }

      TimeRange newRange = TimeRange.fromStartDuration(lowerBound, first - lowerBound);
      lowerBound = second;

      if (newRange.duration() >= duration) {
        timesThatWork.add(newRange);
      }

      if (i == condensedDontWork.size() - 1 && second != 1440) {
        TimeRange range = TimeRange.fromStartDuration(second, 1440 - second);
        if (range.duration() >= duration) {
          timesThatWork.add(range);
        }
      }
    }

    return timesThatWork;
  }

  //public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // //want to find time ranges that the meeting could happen

    // Collection<String> attendees = request.getAttendees();
    // long duration = request.getDuration();

    // System.out.println("duration = " + duration);

    // if (duration > 1440) {
    //   return new ArrayList<TimeRange>();
    // }

    // Collection<TimeRange> timesThatWork = new ArrayList<>();

    // List<TimeRange> timesThatDoNotWork = new ArrayList<>();

    // for (Event event : events) {
    //   TimeRange eventTime = event.getWhen();
    //   Set<String> peopleAtEvent = event.getAttendees();
    //   for (String attendee : attendees) {
    //     if (peopleAtEvent.contains(attendee)) {
    //       timesThatDoNotWork.add(eventTime);
    //     }
    //   }
    // }

    // Collections.sort(timesThatDoNotWork, TimeRange.ORDER_BY_START);
    // int lowerBound = 0;
    // int index = 0;
    // while (lowerBound < 1440 && index < timesThatDoNotWork.size()) {

    // }

  //}


}
