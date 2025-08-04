package test.five.display;

import junit.framework.TestCase;

/**
 * Unit tests for DisplayAdjuster implementations.
 */
public class DisplayAdjusterTest extends TestCase {

    public void testWindowsAdjusterStoresValues() {
        WindowsDisplayAdjuster adjuster = new WindowsDisplayAdjuster();
        adjuster.setBrightness(0.3);
        adjuster.setColorTemperature(4000);
        assertEquals(0.3, adjuster.getBrightness(), 0.0001);
        assertEquals(4000, adjuster.getColorTemperature());
    }

    public void testAndroidAdjusterQueriesActualValues() {
        AndroidTvDisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        adjuster.setBrightness(0.7);
        adjuster.setColorTemperature(5500);
        assertEquals(0.7, adjuster.getBrightness(), 0.0001);
        assertEquals(5500, adjuster.getColorTemperature());

        // Change values directly through Settings and ensure getters read them.
        android.provider.Settings.System.putBrightness(new android.content.Context().getContentResolver(), 0.4);
        android.provider.Settings.System.putColorTemperature(new android.content.Context().getContentResolver(), 4500);
        assertEquals(0.4, adjuster.getBrightness(), 0.0001);
        assertEquals(4500, adjuster.getColorTemperature());
    }

    public void testBrightnessOutOfRangeThrows() {
        DisplayAdjuster adjuster = new WindowsDisplayAdjuster();
        try {
            adjuster.setBrightness(-0.1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            adjuster.setBrightness(1.1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testColorTemperatureOutOfRangeThrows() {
        DisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        try {
            adjuster.setColorTemperature(DisplayAdjuster.MIN_COLOR_TEMPERATURE - 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        try {
            adjuster.setColorTemperature(DisplayAdjuster.MAX_COLOR_TEMPERATURE + 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testResetToDefaults() {
        DisplayAdjuster adjuster = new WindowsDisplayAdjuster();
        adjuster.setBrightness(0.2);
        adjuster.setColorTemperature(3000);
        adjuster.resetToDefaults();
        assertEquals(DisplayAdjuster.DEFAULT_BRIGHTNESS, adjuster.getBrightness(), 0.0001);
        assertEquals(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE, adjuster.getColorTemperature());
    }

    public void testAndroidAdjusterPermissionFailure() {
        AndroidTvDisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        android.provider.Settings.System.setHasWritePermission(false);
        try {
            adjuster.setBrightness(0.5);
            fail("Expected SecurityException");
        } catch (SecurityException expected) {
            // expected
        } finally {
            android.provider.Settings.System.setHasWritePermission(true);
        }
    }

    public void testAndroidAdjusterApiError() {
        AndroidTvDisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        android.provider.Settings.System.setSimulateError(true);
        try {
            adjuster.getBrightness();
            fail("Expected RuntimeException");
        } catch (RuntimeException expected) {
            // expected
        } finally {
            android.provider.Settings.System.setSimulateError(false);
        }
    }
}

