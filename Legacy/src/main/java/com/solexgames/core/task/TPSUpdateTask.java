package com.solexgames.core.task;

public class TPSUpdateTask implements Runnable {

    public int TICK_COUNT = 0;
    public long[] TICKS = new long[600];

    public double getTPS() {
        return getTPS(20);
    }

    public double getTPS(int ticks) {
        if (TICK_COUNT < ticks) {
            return 20.0D;
        }

        final int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        final long elapsed = System.currentTimeMillis() - TICKS[target];

        return ticks / (elapsed / 1000.0D);
    }

    @Override
    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;
    }
}
