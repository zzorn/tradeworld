package org.tradeworld.utils;

import java.lang.management.ManagementFactory;

/**
 * Simple utility class for performance timing etc.
 * Measures CPU time of the current thread.
 */
public final class Stopwatch {

    private int lapsToDiscardLeft = 0;
    private int lapsRecorded = 0;
    private long totalNanoseconds = 0;
    private long lastTimestampNs = 0;

    public Stopwatch() {
        this(0);
    }

    public Stopwatch(int lapsToDiscardLeft) {
        start(lapsToDiscardLeft);
    }

    public void start() {
        start(0);
    }

    public void start(int numberOfInitialLapsToDiscard) {
        lapsToDiscardLeft = numberOfInitialLapsToDiscard;
        lapsRecorded = 0;
        totalNanoseconds = 0;
        lastTimestampNs = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    public void lap() {
        long now = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();

        if (lapsToDiscardLeft > 0) {
            lapsToDiscardLeft--;
        }
        else {
            long lapTime = now - lastTimestampNs;
            totalNanoseconds += lapTime;
            lapsRecorded ++;
        }

        lastTimestampNs = now;
    }

    public double getAverageLapTimeSeconds() {
        if (lapsRecorded <= 0) throw new IllegalStateException("No laps recorded");

        return ((double) (totalNanoseconds / lapsRecorded)) / 1000000000.0;
    }

    public void printResult(String description) {
        System.out.println(description + " took on average " + getAverageLapTimeAsString() + ".");
    }

    private String getAverageLapTimeAsString() {
        double time = getAverageLapTimeSeconds();

        if (time > 60*60) return "" + (time / (60*60)) + " hours";
        if (time > 60) return "" + (time / 60) + " minutes";
        else if (time >= 1) return "" + time + " seconds";
        else if (time >= 0.001) return "" + (time * 1000) + " milliseconds";
        else if (time >= 0.0000001) return "" + (time * 1000000) + " microseconds";
        else return "" + (time * 1000000000) + " nanoseconds";
    }


}
