package test.five.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;

import junit.framework.TestCase;
import test.five.display.WindowsDisplayAdjuster;

/**
 * Tests for the ProfileManager and scheduling logic.
 */
public class ProfileManagerTest extends TestCase {

    public void testProfileCreationAndRetrieval() throws IOException {
        Path temp = Files.createTempFile("profiles", ".json");
        ProfileManager manager = new ProfileManager(temp);
        manager.addProfile(new Profile("alice", 0.5, 5000));
        Profile retrieved = manager.getProfile("alice");
        assertNotNull(retrieved);
        assertEquals(0.5, retrieved.getBrightness(), 0.0001);
        assertEquals(5000, retrieved.getColorTemperature());
    }

    public void testProfilesPersistedToDisk() throws IOException {
        Path temp = Files.createTempFile("profiles", ".json");
        ProfileManager manager = new ProfileManager(temp);
        manager.addProfile(new Profile("bob", 0.7, 4000));
        // New manager should load existing profile
        ProfileManager reloaded = new ProfileManager(temp);
        Profile retrieved = reloaded.getProfile("bob");
        assertNotNull(retrieved);
        assertEquals(0.7, retrieved.getBrightness(), 0.0001);
        assertEquals(4000, retrieved.getColorTemperature());
    }

    public void testScheduledApplication() throws IOException {
        Path temp = Files.createTempFile("profiles", ".json");
        ProfileManager manager = new ProfileManager(temp);
        manager.addProfile(new Profile("carol", 0.3, 6000));
        manager.scheduleProfile("carol", LocalTime.of(6, 0), LocalTime.of(18, 0), "work");
        WindowsDisplayAdjuster adjuster = new WindowsDisplayAdjuster();
        manager.applyScheduledProfile(LocalTime.of(7, 0), "work", adjuster);
        assertEquals(0.3, adjuster.getBrightness(), 0.0001);
        assertEquals(6000, adjuster.getColorTemperature());
    }
}
