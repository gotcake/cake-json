package com.gotcake.json.test;

/**
 * Created by aaron on 9/29/14.
 */
public class Timer {

    private long total;
    private double count;
    private long time;

    public Timer() {
        total = 0;
        count = 0;
        time = 0;
    }

    public void start() {
        time = System.nanoTime();
    }

    public long stop() {
        time = System.nanoTime() - time;
        total += time;
        count++;
        return time;
    }

    public long stopAndLog(String description) {
        time = System.nanoTime() - time;
        total += time;
        count++;
        System.out.println(description + (time/1000000.0) + "ms");
        return time;
    }

    public void clear() { total = 0; count = 0; time = 0; }

    public double average() {
        return total / count;
    }

    public void logAverage(String description) {
        System.out.println(description + (average()/1000000.0) + "ms");
    }
}
