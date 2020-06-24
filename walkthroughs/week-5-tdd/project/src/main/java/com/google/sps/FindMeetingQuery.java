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
    Collection<String> mandatoryAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    List<String> allAttendees = new ArrayList<>(mandatoryAttendees);
    allAttendees.addAll(optionalAttendees);

    long duration = request.getDuration();

    // Requested time is longer than a full day --> return empty list
    if (duration > UPPER_BOUND) {
      return new ArrayList<TimeRange>();
    }

    List<TimeRange> takenTimes = getTakenTimes(events, allAttendees);
    Collection<TimeRange> returnTimesWithOptional = getReturnTimes(allAttendees, takenTimes, duration);
    
    if (returnTimesWithOptional.size() > 0 || mandatoryAttendees.size() == 0) {
      return returnTimesWithOptional;
    } else {
      takenTimes = getTakenTimes(events, mandatoryAttendees);
      return getReturnTimes(mandatoryAttendees, takenTimes, duration);
    }
  }

  // Return blocked off times (when at least one desired attendee is unavailable)
  private List<TimeRange> getTakenTimes(Collection<Event> events, Collection<String> meetingAttendees) {
    List<TimeRange> takenTimes = new ArrayList<>();
    for (Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      for (String attendee : meetingAttendees) {
        if (eventAttendees.contains(attendee)) {
          takenTimes.add(event.getWhen());
          break;
        }
      }
    }
    Collections.sort(takenTimes, TimeRange.ORDER_BY_START);

    return takenTimes;
  }

  // Prereq: takenTimes is already sorted
  private Collection<TimeRange> getReturnTimes(Collection<String> attendees, List<TimeRange> takenTimes, long duration) {
    Collection<TimeRange> returnTimes = new ArrayList<>();

    int eventIndex = 0;
    int lowerBound = 0;
    int upperBound = 0;
    while (lowerBound < UPPER_BOUND && eventIndex <= takenTimes.size()) {
      if (eventIndex == takenTimes.size()) {
        returnTimes.add(TimeRange.fromStartDuration(lowerBound, UPPER_BOUND - lowerBound));
        break;
      }

      TimeRange eventTimeRange = takenTimes.get(eventIndex);
      int start = eventTimeRange.start();
      int end = eventTimeRange.end();

      // If we start at 0, want to jump ahead
      if (lowerBound == start) {
        lowerBound = end;
        continue;
      }

      while (eventIndex < takenTimes.size() - 1) {
        TimeRange range = takenTimes.get(eventIndex + 1);
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
