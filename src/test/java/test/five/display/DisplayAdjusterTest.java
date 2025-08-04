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

    public void testAndroidAdjusterStoresValues() {
        AndroidTvDisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        adjuster.setBrightness(0.7);
        adjuster.setColorTemperature(5500);
        assertEquals(0.7, adjuster.getBrightness(), 0.0001);
        assertEquals(5500, adjuster.getColorTemperature());
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

    public void testFilterActivationAndResetWindows() {
        WindowsDisplayAdjuster adjuster = new WindowsDisplayAdjuster();
        adjuster.enableWarmToneFilter(true);
        adjuster.enableGrayscaleFilter(true);
        assertTrue(adjuster.isWarmToneFilterEnabled());
        assertTrue(adjuster.isGrayscaleFilterEnabled());
        adjuster.resetToDefaults();
        assertFalse(adjuster.isWarmToneFilterEnabled());
        assertFalse(adjuster.isGrayscaleFilterEnabled());
    }

    public void testFilterActivationAndResetAndroid() {
        AndroidTvDisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
        adjuster.enableWarmToneFilter(true);
        adjuster.enableGrayscaleFilter(true);
        assertTrue(adjuster.isWarmToneFilterEnabled());
        assertTrue(adjuster.isGrayscaleFilterEnabled());
        adjuster.resetToDefaults();
        assertFalse(adjuster.isWarmToneFilterEnabled());
        assertFalse(adjuster.isGrayscaleFilterEnabled());
    }
}
