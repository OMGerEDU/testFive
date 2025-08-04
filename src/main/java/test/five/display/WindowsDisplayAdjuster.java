package test.five.display;

/**
 * Windows implementation of the {@link DisplayAdjuster} interface. Real
 * interactions with the operating system are delegated to a
 * {@link WindowsDisplayLibrary} instance which is responsible for calling
 * Windows.Graphics.Display or DirectX APIs. This class performs parameter
 * validation and translates any {@link UnsupportedOperationException}
 * from the native layer into descriptive {@link IllegalStateException}s.
 */
public class WindowsDisplayAdjuster implements DisplayAdjuster {
    private final WindowsDisplayLibrary nativeLib;

    /**
     * Creates an adjuster that delegates to the default native library
     * implementation.
     */
    public WindowsDisplayAdjuster() {
        this(new WindowsDisplayLibraryImpl());
    }

    /**
     * Visible for testing: allows injection of a mock native library.
     */
    WindowsDisplayAdjuster(WindowsDisplayLibrary nativeLib) {
        this.nativeLib = nativeLib;
    }

    @Override
    public void setBrightness(double level) {
        if (level < MIN_BRIGHTNESS || level > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "Brightness must be between " + MIN_BRIGHTNESS + " and " + MAX_BRIGHTNESS);
        }
        try {
            nativeLib.setBrightness(level);
        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException("Brightness control unsupported on this hardware", e);
        }
    }

    @Override
    public double getBrightness() {
        try {
            return nativeLib.getBrightness();
        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException("Brightness query unsupported on this hardware", e);
        }
    }

    @Override
    public void setColorTemperature(int temperature) {
        if (temperature < MIN_COLOR_TEMPERATURE || temperature > MAX_COLOR_TEMPERATURE) {
            throw new IllegalArgumentException(
                    "Temperature must be between " + MIN_COLOR_TEMPERATURE + " and " + MAX_COLOR_TEMPERATURE);
        }
        try {
            nativeLib.setColorTemperature(temperature);
        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException("Color temperature control unsupported on this hardware", e);
        }
    }

    @Override
    public int getColorTemperature() {
        try {
            return nativeLib.getColorTemperature();
        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException("Color temperature query unsupported on this hardware", e);
        }
    }

    @Override
    public void resetToDefaults() {
        setBrightness(DEFAULT_BRIGHTNESS);
        setColorTemperature(DEFAULT_COLOR_TEMPERATURE);
    }
}
