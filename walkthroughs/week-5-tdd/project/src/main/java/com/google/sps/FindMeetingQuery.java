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

  private List<TimeRange> getTimesThatDontWork(Collection<Event> events, Collection<String> attendees) {
    List<TimeRange> blockedTimes = new ArrayList<>();

    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      Set<String> peopleAtEvent = event.getAttendees();
      for (String attendee : attendees) {
        if (peopleAtEvent.contains(attendee)) {
          blockedTimes.add(eventTime);
        }
      }
    }

    return blockedTimes;
  } 

  private List<TimeRange> condenseTimeRanges(List<TimeRange> blockedTimes) {
    ArrayList<TimeRange> condensedBlockedTimes = new ArrayList<>();
    for (int i = 0; i < blockedTimes.size(); i++) {
      TimeRange first = blockedTimes.get(i);

      if (i == blockedTimes.size() - 1) {
        condensedBlockedTimes.add(first);
        break;
      }
      TimeRange second = blockedTimes.get(i + 1);

      if (first.overlaps(second)) { //can condense
        int firstEnd = first.end();
        int secondEnd = second.end();
        int newStart = first.start();
        
        int newEnd = secondEnd;
        if (firstEnd > secondEnd) {
          newEnd = firstEnd;
        } 

        i++;
        condensedBlockedTimes.add(TimeRange.fromStartDuration(newStart, newEnd - newStart));
      } else {
        condensedBlockedTimes.add(first);
      }
    }
    return condensedBlockedTimes;
  }

  public Collection<TimeRange> otherway(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();
    if (duration > UPPER_BOUND) {
      return new ArrayList<TimeRange>();
    }

    List<TimeRange> blockedTimes = getTimesThatDontWork(events, attendees);
    Collections.sort(blockedTimes, TimeRange.ORDER_BY_START);

    //Condense times that are not available
    List<TimeRange> condensedBlockedTimes = condenseTimeRanges(blockedTimes);

    int lowerBound = LOWER_BOUND;
    Collection<TimeRange> timesThatWork = new ArrayList<>();
    if (condensedBlockedTimes.size() == 0) {
      timesThatWork.add(TimeRange.fromStartDuration(LOWER_BOUND, UPPER_BOUND));
    }

    for (int i = 0; i < condensedBlockedTimes.size(); i++) {
      int first = condensedBlockedTimes.get(i).start();
      int second = condensedBlockedTimes.get(i).end();

      if (first == 0) {
        lowerBound = second;
        continue;
      }

      TimeRange newRange = TimeRange.fromStartDuration(lowerBound, first - lowerBound);
      lowerBound = second;

      if (newRange.duration() >= duration) {
        timesThatWork.add(newRange);
      }

      if (i == condensedBlockedTimes.size() - 1 && second != UPPER_BOUND) {
        TimeRange range = TimeRange.fromStartDuration(second, UPPER_BOUND - second);
        if (range.duration() >= duration) {
          timesThatWork.add(range);
        }
      }
    }
    return timesThatWork;
  } 

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();
    if (duration > UPPER_BOUND) {
      return new ArrayList<TimeRange>();
    }

    Collection<TimeRange> returnTimes = new ArrayList<>();

    List<Event> eventsList = new ArrayList<>(events);
    int eventIndex = 0;
    int lowerBound = 0;
    int upperBound = 0;
    while (lowerBound < 1440 && eventIndex <= eventsList.size()) {
      if (eventIndex == eventsList.size()) {
        returnTimes.add(TimeRange.fromStartDuration(lowerBound, 1440 - lowerBound));
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

      // Ignore this event -- no people of interest
      if (noAttendeesAtEvent) { 
        eventIndex++;
        continue;
      }

      // If we start at 0, want to jump ahead
      if (lowerBound == start) {
        lowerBound = end;
        continue;
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
