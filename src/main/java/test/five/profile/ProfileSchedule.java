package test.five.profile;

import java.time.LocalTime;

/**
 * Associates a user profile with a time-of-day range and an optional activity name.
 */
public class ProfileSchedule {
    private final String user;
    private final LocalTime start;
    private final LocalTime end;
    private final String activity; // may be null for any activity

    public ProfileSchedule(String user, LocalTime start, LocalTime end, String activity) {
        this.user = user;
        this.start = start;
        this.end = end;
        this.activity = activity;
    }

    public String getUser() {
        return user;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getActivity() {
        return activity;
    }

    /**
     * Determine whether this schedule is active for the provided time and activity.
     *
     * @param time current time
     * @param currentActivity current activity name, may be null
     * @return true if the schedule should apply
     */
    public boolean matches(LocalTime time, String currentActivity) {
        boolean withinTime = (time.equals(start) || time.isAfter(start)) && time.isBefore(end);
        boolean activityMatch = activity == null || activity.equals(currentActivity);
        return withinTime && activityMatch;
    }
}
