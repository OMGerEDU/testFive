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
        Map<String, Object> root = new HashMap<>();
        root.put("profiles", new ArrayList<>(profiles.values()));
        List<Map<String, Object>> scheduleRecords = new ArrayList<>();
        for (ProfileSchedule schedule : schedules) {
            Map<String, Object> rec = new HashMap<>();
            rec.put("user", schedule.getUser());
            rec.put("start", schedule.getStart().toString());
            rec.put("end", schedule.getEnd().toString());
            rec.put("activity", schedule.getActivity());
            scheduleRecords.add(rec);
        }
        root.put("schedules", scheduleRecords);
        try {
            Files.writeString(storagePath, SimpleJson.stringify(root), StandardCharsets.UTF_8);
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
            String content = Files.readString(storagePath, StandardCharsets.UTF_8);
            Object parsed = SimpleJson.parse(content);
            if (!(parsed instanceof Map)) {
                return;
            }
            Map<?,?> root = (Map<?,?>) parsed;
            Object p = root.get("profiles");
            if (p instanceof List<?> list) {
                for (Object obj : list) {
                    if (obj instanceof Map<?,?> m) {
                        String user = (String) m.get("user");
                        double brightness = ((Number) m.get("brightness")).doubleValue();
                        int color = ((Number) m.get("colorTemperature")).intValue();
                        profiles.put(user, new Profile(user, brightness, color));
                    }
                }
            }
            Object s = root.get("schedules");
            if (s instanceof List<?> list) {
                for (Object obj : list) {
                    if (obj instanceof Map<?,?> m) {
                        String user = (String) m.get("user");
                        LocalTime start = LocalTime.parse((String) m.get("start"));
                        LocalTime end = LocalTime.parse((String) m.get("end"));
                        String activity = (String) m.get("activity");
                        schedules.add(new ProfileSchedule(user, start, end, activity));
                    }
                }
            }
        } catch (IOException e) {
            // ignore errors in this simple implementation
        }
    }
}
