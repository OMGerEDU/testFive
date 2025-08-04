package test.five.display;

import junit.framework.TestCase;

/**
 * Unit tests for DisplayAdjuster implementations.
 */
public class DisplayAdjusterTest extends TestCase {

    /**
     * Simple fake implementation that mimics the native Windows API for tests.
     */
    private static class FakeWindowsDisplayLibrary implements WindowsDisplayLibrary {
        double brightness = DisplayAdjuster.DEFAULT_BRIGHTNESS;
        int colorTemperature = DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE;
        boolean brightnessSupported = true;
        boolean colorTempSupported = true;

        @Override
        public double getBrightness() {
            if (!brightnessSupported) {
                throw new UnsupportedOperationException("brightness unsupported");
            }
            return brightness;
        }

        @Override
        public void setBrightness(double level) {
            if (!brightnessSupported) {
                throw new UnsupportedOperationException("brightness unsupported");
            }
            brightness = level;
        }

        @Override
        public int getColorTemperature() {
            if (!colorTempSupported) {
                throw new UnsupportedOperationException("color temp unsupported");
            }
            return colorTemperature;
        }

        @Override
        public void setColorTemperature(int temperature) {
            if (!colorTempSupported) {
                throw new UnsupportedOperationException("color temp unsupported");
            }
            colorTemperature = temperature;
        }
    }

    public void testWindowsAdjusterDelegatesToNative() {
        FakeWindowsDisplayLibrary lib = new FakeWindowsDisplayLibrary();
        WindowsDisplayAdjuster adjuster = new WindowsDisplayAdjuster(lib);
        adjuster.setBrightness(0.3);
        adjuster.setColorTemperature(4000);
        assertEquals(0.3, lib.brightness, 0.0001);
        assertEquals(4000, lib.colorTemperature);

        // verify getters read from native layer
        lib.brightness = 0.6;
        lib.colorTemperature = 5000;
        assertEquals(0.6, adjuster.getBrightness(), 0.0001);
        assertEquals(5000, adjuster.getColorTemperature());
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
        DisplayAdjuster adjuster = new WindowsDisplayAdjuster(new FakeWindowsDisplayLibrary());
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
        FakeWindowsDisplayLibrary lib = new FakeWindowsDisplayLibrary();
        DisplayAdjuster adjuster = new WindowsDisplayAdjuster(lib);
        adjuster.setBrightness(0.2);
        adjuster.setColorTemperature(3000);
        adjuster.resetToDefaults();
        assertEquals(DisplayAdjuster.DEFAULT_BRIGHTNESS, lib.brightness, 0.0001);
        assertEquals(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE, lib.colorTemperature);
    }

    public void testUnsupportedHardwareThrowsDescriptiveException() {
        FakeWindowsDisplayLibrary lib = new FakeWindowsDisplayLibrary();
        lib.brightnessSupported = false;
        WindowsDisplayAdjuster adjuster = new WindowsDisplayAdjuster(lib);
        try {
            adjuster.setBrightness(0.5);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().toLowerCase().contains("brightness"));
        }

        lib = new FakeWindowsDisplayLibrary();
        lib.colorTempSupported = false;
        adjuster = new WindowsDisplayAdjuster(lib);
        try {
            adjuster.getColorTemperature();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().toLowerCase().contains("color temperature"));
        }
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

