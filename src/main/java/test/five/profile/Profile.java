package test.five.profile;

/**
 * Represents a saved user profile containing brightness and
 * color temperature settings.
 */
public class Profile {
    private final String user;
    private final double brightness;
    private final int colorTemperature;

    public Profile(String user, double brightness, int colorTemperature) {
        this.user = user;
        this.brightness = brightness;
        this.colorTemperature = colorTemperature;
    }

    public String getUser() {
        return user;
    }

    public double getBrightness() {
        return brightness;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }
}
