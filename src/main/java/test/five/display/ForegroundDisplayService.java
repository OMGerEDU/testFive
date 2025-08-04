package test.five.display;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simplified foreground service stub to illustrate continuous control.
 * In a real Android environment this would extend Service and run in the
 * foreground. Here it merely tracks whether it has been started.
 */
public class ForegroundDisplayService implements Runnable {
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private final AndroidTvDisplayAdjuster adjuster;

    private ForegroundDisplayService(AndroidTvDisplayAdjuster adjuster) {
        this.adjuster = adjuster;
    }

    /** Start the service. */
    public static ForegroundDisplayService start(AndroidTvDisplayAdjuster adjuster) {
        RUNNING.set(true);
        return new ForegroundDisplayService(adjuster);
    }

    /** Stop the service. */
    public void stop() {
        RUNNING.set(false);
    }

    /** Whether the service is currently running. */
    public static boolean isRunning() {
        return RUNNING.get();
    }

    @Override
    public void run() {
        // No continuous loop required for the stub implementation.
    }
}

