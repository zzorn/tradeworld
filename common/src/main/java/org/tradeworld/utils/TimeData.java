package org.tradeworld.utils;

/**
 *
 */
public class TimeData {

    private long millisecondsSinceStart = 0;
    private long lastFrameTimeStamp;
    private long lastFrameDurationMs;
    private double lastFrameDurationSeconds;

    public TimeData() {
        lastFrameTimeStamp = System.currentTimeMillis();
    }

    /**
     * Call this every frame.
     */
    public void onFrame() {
        long time = System.currentTimeMillis();
        lastFrameDurationMs = Math.max(0, time - lastFrameTimeStamp);
        millisecondsSinceStart += lastFrameDurationMs;
        lastFrameDurationSeconds = lastFrameDurationMs * 0.001;
    }

    public double getLastFrameDurationSeconds() {
        return lastFrameDurationSeconds;
    }

    public long getLastFrameDurationMs() {
        return lastFrameDurationMs;
    }

    public long getMillisecondsSinceStart() {
        return millisecondsSinceStart;
    }
}
