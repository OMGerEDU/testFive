package test.five.display;

import android.content.Context;
import android.content.ContentResolver;
import android.provider.Settings;

/**
 * Android TV implementation of the DisplayAdjuster interface.
 * This version interacts with a stub of Android's Settings provider
 * and supports starting a foreground service for continuous control.
 */
public class AndroidTvDisplayAdjuster implements DisplayAdjuster {
    private final ContentResolver resolver;

    /**
     * Create an adjuster using a new stub Context instance.
     */
    public AndroidTvDisplayAdjuster() {
        this(new Context());
    }

    /**
     * Create an adjuster using the provided context.
     */
    public AndroidTvDisplayAdjuster(Context context) {
        this.resolver = context.getContentResolver();
    }

    @Override
    public void setBrightness(double level) {
        if (level < MIN_BRIGHTNESS || level > MAX_BRIGHTNESS) {
            throw new IllegalArgumentException(
                    "Brightness must be between " + MIN_BRIGHTNESS + " and " + MAX_BRIGHTNESS);
        }
        Settings.System.putBrightness(resolver, level);
    }

    @Override
    public double getBrightness() {
        return Settings.System.getBrightness(resolver);
    }

    @Override
    public void setColorTemperature(int temperature) {
        if (temperature < MIN_COLOR_TEMPERATURE || temperature > MAX_COLOR_TEMPERATURE) {
            throw new IllegalArgumentException(
                    "Temperature must be between " + MIN_COLOR_TEMPERATURE + " and " + MAX_COLOR_TEMPERATURE);
        }
        Settings.System.putColorTemperature(resolver, temperature);
    }

    @Override
    public int getColorTemperature() {
        return Settings.System.getColorTemperature(resolver);
    }

    /**
     * Start a foreground service to maintain continuous display control.
     */
    public ForegroundDisplayService startContinuousControl() {
        return ForegroundDisplayService.start(this);
    }

    @Override
    public void resetToDefaults() {
        setBrightness(DEFAULT_BRIGHTNESS);
        setColorTemperature(DEFAULT_COLOR_TEMPERATURE);
    }
}

