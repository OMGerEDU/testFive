package test.five.display;

/**
 * Cross-platform interface for adjusting display parameters.
 * Implementations should handle platform specific APIs for
 * brightness and color temperature adjustments.
 */
public interface DisplayAdjuster {
    /** Minimum allowed brightness level. */
    double MIN_BRIGHTNESS = 0.0;
    /** Maximum allowed brightness level. */
    double MAX_BRIGHTNESS = 1.0;
    /** Default brightness level used by {@link #resetToDefaults()}. */
    double DEFAULT_BRIGHTNESS = MAX_BRIGHTNESS;
    /** Minimum allowed color temperature in Kelvin. */
    int MIN_COLOR_TEMPERATURE = 1_000;
    /** Maximum allowed color temperature in Kelvin. */
    int MAX_COLOR_TEMPERATURE = 10_000;
    /** Default color temperature in Kelvin used by {@link #resetToDefaults()}. */
    int DEFAULT_COLOR_TEMPERATURE = 6_500;
    /** Threshold for detecting excessive flicker in flashes per second. */
    int FLICKER_THRESHOLD = 30;

    /**
     * Set the display brightness level.
     *
     * @param level brightness value from {@link #MIN_BRIGHTNESS} to
     *              {@link #MAX_BRIGHTNESS}
     * @throws IllegalArgumentException if the level is outside the allowed range
     */
    void setBrightness(double level);

    /**
     * Get the current brightness level.
     *
     * @return brightness value from 0.0 to 1.0
     */
    double getBrightness();

    /**
     * Set the color temperature.
     *
     * @param temperature Kelvin value for color temperature in the range
     *                    {@link #MIN_COLOR_TEMPERATURE} –
     *                    {@link #MAX_COLOR_TEMPERATURE}
     * @throws IllegalArgumentException if the temperature is outside the allowed range
     */
    void setColorTemperature(int temperature);

    /**
     * Get the current color temperature.
     *
     * @return Kelvin value representing the color temperature
     */
    int getColorTemperature();

    /**
     * Enable or disable a warm-tone color filter.
     * Implementations may apply platform-specific overlays or shaders.
     *
     * @param enable {@code true} to enable the warm-tone filter
     */
    void enableWarmToneFilter(boolean enable);

    /**
     * @return {@code true} if the warm-tone filter is currently enabled
     */
    boolean isWarmToneFilterEnabled();

    /**
     * Enable or disable a grayscale color filter.
     *
     * @param enable {@code true} to enable the grayscale filter
     */
    void enableGrayscaleFilter(boolean enable);

    /**
     * @return {@code true} if the grayscale filter is currently enabled
     */
    boolean isGrayscaleFilterEnabled();

    /**
     * Enable or disable automatic flicker detection.
     *
     * @param enable {@code true} to enable flicker detection
     */
    void setFlickerDetectionEnabled(boolean enable);

    /**
     * Analyze the current flicker rate and lower brightness if excessive
     * flashing is detected.
     *
     * @param flashesPerSecond observed flash frequency
     */
    void detectFlicker(int flashesPerSecond);

    /**
     * Reset both brightness and color temperature to safe defaults.
     */
    void resetToDefaults();
}
