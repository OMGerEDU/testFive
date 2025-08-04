package android.provider;

import android.content.ContentResolver;
import test.five.display.DisplayAdjuster;

/**
 * Minimal stub of Android's Settings provider. This class stores
 * brightness and color temperature values in static fields and
 * simulates permission checks and API failures for testing.
 */
public class Settings {
    public static class System {
        private static double brightness = DisplayAdjuster.DEFAULT_BRIGHTNESS;
        private static int colorTemperature = DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE;
        private static boolean hasWritePermission = true;
        private static boolean simulateError = false;

        /** Set whether write operations should fail with SecurityException. */
        public static void setHasWritePermission(boolean has) {
            hasWritePermission = has;
        }

        /** Set whether any operation should fail with RuntimeException. */
        public static void setSimulateError(boolean error) {
            simulateError = error;
        }

        public static double getBrightness(ContentResolver resolver) {
            if (simulateError) {
                throw new RuntimeException("Failed to read brightness");
            }
            return brightness;
        }

        public static boolean putBrightness(ContentResolver resolver, double value) {
            if (simulateError) {
                throw new RuntimeException("Failed to write brightness");
            }
            if (!hasWritePermission) {
                throw new SecurityException("WRITE_SETTINGS permission required");
            }
            brightness = value;
            return true;
        }

        public static int getColorTemperature(ContentResolver resolver) {
            if (simulateError) {
                throw new RuntimeException("Failed to read color temperature");
            }
            return colorTemperature;
        }

        public static boolean putColorTemperature(ContentResolver resolver, int value) {
            if (simulateError) {
                throw new RuntimeException("Failed to write color temperature");
            }
            if (!hasWritePermission) {
                throw new SecurityException("WRITE_SETTINGS permission required");
            }
            colorTemperature = value;
            return true;
        }
    }
}

