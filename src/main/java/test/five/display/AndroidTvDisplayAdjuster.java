package test.five.display;

/**
 * Android TV implementation of the DisplayAdjuster interface.
 * Like the Windows implementation, this is currently a stub
 * that records values rather than interfacing with Android APIs.
 */
public class AndroidTvDisplayAdjuster implements DisplayAdjuster {
    private double brightness = MAX_BRIGHTNESS;
    private int colorTemperature = 6500;
    private boolean warmToneEnabled = false;
    private boolean grayscaleEnabled = false;
    private boolean flickerDetectionEnabled = false;

    @Override
    public void setBrightness(double level) {
        if (level < MIN_BRIGHTNESS || level > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "Brightness must be between " + MIN_BRIGHTNESS + " and " + MAX_BRIGHTNESS);
        }
        this.brightness = level;
    }

    @Override
    public double getBrightness() {
        return brightness;
    }

    @Override
    public void setColorTemperature(int temperature) {
        if (temperature < MIN_COLOR_TEMPERATURE || temperature > MAX_COLOR_TEMPERATURE) {
            throw new IllegalArgumentException(
                    "Temperature must be between " + MIN_COLOR_TEMPERATURE + " and " + MAX_COLOR_TEMPERATURE);
        }
        this.colorTemperature = temperature;
    }

    @Override
    public int getColorTemperature() {
        return colorTemperature;
    }

    @Override
    public void enableWarmToneFilter(boolean enable) {
        warmToneEnabled = enable;
        if (enable) {
            applyAndroidWarmToneShader();
        }
    }

    @Override
    public boolean isWarmToneFilterEnabled() {
        return warmToneEnabled;
    }

    @Override
    public void enableGrayscaleFilter(boolean enable) {
        grayscaleEnabled = enable;
        if (enable) {
            applyAndroidGrayscaleShader();
        }
    }

    @Override
    public boolean isGrayscaleFilterEnabled() {
        return grayscaleEnabled;
    }

    @Override
    public void setFlickerDetectionEnabled(boolean enable) {
        flickerDetectionEnabled = enable;
    }

    @Override
    public void detectFlicker(int flashesPerSecond) {
        if (flickerDetectionEnabled && flashesPerSecond > FLICKER_THRESHOLD) {
            brightness = Math.max(MIN_BRIGHTNESS, brightness * 0.5);
        }
    }

    @Override
    public void resetToDefaults() {
        brightness = DEFAULT_BRIGHTNESS;
        colorTemperature = DEFAULT_COLOR_TEMPERATURE;
        warmToneEnabled = false;
        grayscaleEnabled = false;
        flickerDetectionEnabled = false;
    }

    private void applyAndroidWarmToneShader() {
        // Stub for Android-specific warm-tone GPU shader.
    }

    private void applyAndroidGrayscaleShader() {
        // Stub for Android-specific grayscale GPU shader.
    }
}
