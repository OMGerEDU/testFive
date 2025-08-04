package test.five.profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.five.display.DisplayAdjuster;

/**
 * Manages user profiles and their associated schedules. Profiles are persisted
 * to disk as a small JSON document and loaded at construction time.
 */
public class ProfileManager {
    private final Map<String, Profile> profiles = new HashMap<>();
    private final List<ProfileSchedule> schedules = new ArrayList<>();
    private final Path storagePath;

    public ProfileManager(Path storagePath) {
        this.storagePath = storagePath;
        load();
    }

    /**
     * Add or replace a profile for a user. The profile is immediately persisted
     * to disk.
     */
    public void addProfile(Profile profile) {
        profiles.put(profile.getUser(), profile);
        save();
    }

    /** Retrieve a profile for a given user. */
    public Profile getProfile(String user) {
        return profiles.get(user);
    }

    /**
     * Schedule a user's profile for a time-of-day range and optional activity.
     * The schedule information is persisted to disk.
     */
    public void scheduleProfile(String user, LocalTime start, LocalTime end, String activity) {
        schedules.add(new ProfileSchedule(user, start, end, activity));
        save();
    }

    /**
     * Apply the scheduled profile for the given time and activity using the provided adjuster.
     * If no schedule matches, nothing is applied.
     */
    public void applyScheduledProfile(LocalTime time, String activity, DisplayAdjuster adjuster) {
        for (ProfileSchedule schedule : schedules) {
            if (schedule.matches(time, activity)) {
                Profile profile = profiles.get(schedule.getUser());
                if (profile != null) {
                    adjuster.setBrightness(profile.getBrightness());
                    adjuster.setColorTemperature(profile.getColorTemperature());
                }
                return;
            }
        }
    }

    /** Persist profiles and schedules to disk as JSON. */
    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"profiles\":[");
        boolean first = true;
        for (Profile profile : profiles.values()) {
            if (!first) sb.append(',');
            sb.append("{\"user\":\"").append(profile.getUser()).append("\",")
              .append("\"brightness\":").append(profile.getBrightness()).append(',')
              .append("\"colorTemperature\":").append(profile.getColorTemperature()).append('}');
            first = false;
        }
        sb.append("],\"schedules\":[");
        first = true;
        for (ProfileSchedule schedule : schedules) {
            if (!first) sb.append(',');
            sb.append("{\"user\":\"").append(schedule.getUser()).append("\",")
              .append("\"start\":\"").append(schedule.getStart()).append("\",")
              .append("\"end\":\"").append(schedule.getEnd()).append("\",")
              .append("\"activity\":");
            if (schedule.getActivity() == null) {
                sb.append("null");
            } else {
                sb.append('\"').append(schedule.getActivity()).append('\"');
            }
            sb.append('}');
            first = false;
        }
        sb.append("]}");
        try {
            Files.write(storagePath, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Swallowing exception for simplicity in this stub implementation
        }
    }

    /** Load profiles and schedules from disk if the storage file exists. */
    private void load() {
        profiles.clear();
        schedules.clear();
        if (!Files.exists(storagePath)) {
            return;
        }
        try {
            String content = new String(Files.readAllBytes(storagePath), StandardCharsets.UTF_8);
            Pattern profilePattern = Pattern.compile("\\{\\\"user\\\":\\\"(.*?)\\\",\\\"brightness\\\":(.*?),\\\"colorTemperature\\\":(.*?)\\}");
            Matcher pm = profilePattern.matcher(content);
            while (pm.find()) {
                String user = pm.group(1);
                double brightness = Double.parseDouble(pm.group(2));
                int color = Integer.parseInt(pm.group(3));
                profiles.put(user, new Profile(user, brightness, color));
            }
            Pattern schedulePattern = Pattern.compile("\\{\\\"user\\\":\\\"(.*?)\\\",\\\"start\\\":\\\"(.*?)\\\",\\\"end\\\":\\\"(.*?)\\\",\\\"activity\\\":(null|\\\"(.*?)\\\")\\}");
            Matcher sm = schedulePattern.matcher(content);
            while (sm.find()) {
                String user = sm.group(1);
                LocalTime start = LocalTime.parse(sm.group(2));
                LocalTime end = LocalTime.parse(sm.group(3));
                String rawActivity = sm.group(4);
                String activity = "null".equals(rawActivity) ? null : sm.group(5);
                schedules.add(new ProfileSchedule(user, start, end, activity));
            }
        } catch (IOException e) {
            // ignore errors in this simple implementation
        }
    }
}
