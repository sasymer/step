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

  private static final int UPPER_BOUND = 1440;
  private static final int LOWER_BOUND = 0;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    List<String> allAttendees = new ArrayList<>(attendees);
    allAttendees.addAll(optionalAttendees);

    long duration = request.getDuration();
    if (duration > UPPER_BOUND) {
      return new ArrayList<TimeRange>();
    }

    Collection<TimeRange> returnTimesWithOptional = getReturnTimes(allAttendees, events, duration);
    
    if (returnTimesWithOptional.size() > 0 || attendees.size() == 0) {
      return returnTimesWithOptional;
    } else {
      return getReturnTimes(attendees, events, duration);
    }
  }

  private Collection<TimeRange> getReturnTimes(Collection<String> attendees, Collection<Event> events, long duration) {
    Collection<TimeRange> returnTimes = new ArrayList<>();
    List<Event> eventsList = new ArrayList<>(events);
    int eventIndex = 0;
    int lowerBound = 0;
    int upperBound = 0;
    while (lowerBound < UPPER_BOUND && eventIndex <= eventsList.size()) {
      if (eventIndex == eventsList.size()) {
        returnTimes.add(TimeRange.fromStartDuration(lowerBound, UPPER_BOUND - lowerBound));
        break;
      }

      Event event = eventsList.get(eventIndex);
      Set<String> eventAttendees = event.getAttendees();
      boolean noAttendeesAtEvent = true;
      for (String attendee : attendees) {
        if (eventAttendees.contains(attendee)) {
          noAttendeesAtEvent = false;
        }
      }

      TimeRange eventTimeRange = event.getWhen();
      int start = eventTimeRange.start();
      int end = eventTimeRange.end();

      // Ignore this event -- no people of interest - could be optional attendees here only
      if (noAttendeesAtEvent) { 
        eventIndex++;
        continue;
      }

      // If we start at 0, want to jump ahead
      if (lowerBound == start) {
        lowerBound = end;
        continue;
      }

      while (eventIndex < eventsList.size() - 1) {
        Event nextEvent = eventsList.get(eventIndex + 1);
        TimeRange range = nextEvent.getWhen();
        if (range.start() < end) {
          if (range.end() > end) { //nested
            end = range.end();
          } 
          eventIndex++;
        } else {
          break;
        }
      }

      upperBound = start;
      TimeRange goodTime = TimeRange.fromStartDuration(lowerBound, upperBound - lowerBound);
      lowerBound = end;

      if (goodTime.duration() >= duration) {
        returnTimes.add(goodTime);
      }
      eventIndex++;
    }
    return returnTimes;
  }
}
