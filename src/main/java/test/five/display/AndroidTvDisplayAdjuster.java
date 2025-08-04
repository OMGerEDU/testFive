package test.five.display;

/**
 * Android TV implementation of the DisplayAdjuster interface.
 * Like the Windows implementation, this is currently a stub
 * that records values rather than interfacing with Android APIs.
 */
public class AndroidTvDisplayAdjuster implements DisplayAdjuster {
    private double brightness = MAX_BRIGHTNESS;
    private int colorTemperature = 6500;

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
