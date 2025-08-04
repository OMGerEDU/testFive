package test.five.display;

/**
 * Default implementation of {@link WindowsDisplayLibrary} that would invoke
 * Windows native APIs. The actual native calls are not provided in this
 * environment; instead the methods throw {@link UnsupportedOperationException}
 * indicating that the functionality is unavailable.
 */
public class WindowsDisplayLibraryImpl implements WindowsDisplayLibrary {
    @Override
    public double getBrightness() {
        throw new UnsupportedOperationException("Native brightness retrieval not implemented");
    }

    @Override
    public void setBrightness(double level) {
        throw new UnsupportedOperationException("Native brightness adjustment not implemented");
    }

    @Override
    public int getColorTemperature() {
        throw new UnsupportedOperationException("Native color temperature retrieval not implemented");
    }

    @Override
    public void setColorTemperature(int temperature) {
        throw new UnsupportedOperationException("Native color temperature adjustment not implemented");
    }
}
