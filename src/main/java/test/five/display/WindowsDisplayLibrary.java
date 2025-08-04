package test.five.display;

/**
 * Abstraction over native Windows display APIs such as Windows.Graphics.Display
 * or DirectX. Implementations are expected to bridge to the real operating
 * system and may throw {@link UnsupportedOperationException} when the hardware
 * does not support a given operation.
 */
public interface WindowsDisplayLibrary {
    /**
     * @return current display brightness in the range 0.0 – 1.0
     * @throws UnsupportedOperationException if the hardware does not expose brightness controls
     */
    double getBrightness();

    /**
     * @param level brightness value in the range 0.0 – 1.0
     * @throws UnsupportedOperationException if the hardware does not support setting brightness
     */
    void setBrightness(double level);

    /**
     * @return current color temperature in Kelvin
     * @throws UnsupportedOperationException if the hardware does not expose color temperature controls
     */
    int getColorTemperature();

    /**
     * @param temperature color temperature in Kelvin
     * @throws UnsupportedOperationException if the hardware does not support setting color temperature
     */
    void setColorTemperature(int temperature);
}
