package test.five.display;

/**
 * Windows implementation of the DisplayAdjuster interface.
 * This class currently contains stubbed behavior and does not
 * interact with native Windows APIs. It records the values that
 * would be applied to the system.
 */
public class WindowsDisplayAdjuster implements DisplayAdjuster {
    private double brightness = MAX_BRIGHTNESS;
    private int colorTemperature = 6500; // neutral white

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
    public void resetToDefaults() {
        brightness = DEFAULT_BRIGHTNESS;
        colorTemperature = DEFAULT_COLOR_TEMPERATURE;
    }
}
