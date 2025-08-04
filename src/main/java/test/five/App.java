package test.five;

import test.five.display.AndroidTvDisplayAdjuster;
import test.five.display.DisplayAdjuster;
import test.five.display.WindowsDisplayAdjuster;

/**
 * Entry point for the prototype application.
 * It selects a DisplayAdjuster implementation based on the
 * current operating system and applies sample settings.
 */
public class App {
    public static void main(String[] args) {
        DisplayAdjuster adjuster;
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            adjuster = new WindowsDisplayAdjuster();
        } else {
            adjuster = new AndroidTvDisplayAdjuster();
        }

        // Apply sample settings
        adjuster.setBrightness(0.5);
        adjuster.setColorTemperature(5000);

        System.out.println("Brightness: " + adjuster.getBrightness());
        System.out.println("Color Temperature: " + adjuster.getColorTemperature());

        // Reset to safe defaults and display them
        adjuster.resetToDefaults();
        System.out.println("Reset Brightness: " + adjuster.getBrightness());
        System.out.println("Reset Color Temperature: " + adjuster.getColorTemperature());
    }
}
